package io.lastwill;

import lombok.Cleanup;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.impl.client.CloseableHttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterNumber;
import org.web3j.protocol.core.methods.response.EthBlock;
import org.web3j.protocol.http.HttpService;

import javax.annotation.PostConstruct;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@Component
public class Exporter implements ApplicationRunner {
    @Value("${io.lastwill.jdbc-url}")
    private String JDBC_URL;

    @Value("${io.lastwill.web3-url}")
    private String WEB3_URL;

    @Value("${io.lastwill.exporter.pool-size:10}")
    private int poolSize;

    @Autowired
    private CloseableHttpClient closeableHttpClient;

    @Autowired
    private Persister persister;

    private final AtomicBoolean terminated = new AtomicBoolean(false);
    private final Object waitExit = new Object();
    private Semaphore semaphore;
    private final Queue<EthBlock> resultQueue = new ConcurrentLinkedQueue<>();

    @PostConstruct
    protected void init() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            log.info("Termination caught, wait till batch will be saved.");
            terminated.set(true);
            try {
                synchronized (waitExit) {
                    waitExit.wait();
                }
            }
            catch (InterruptedException e) {
                log.error("Wait was terminated! It might lead to loose transactions.", e);
            }
        }));
        log.info("Shutdown hook deployed.");
        semaphore = new Semaphore(poolSize);
    }

    @Override
    public void run(ApplicationArguments applicationArguments) throws Exception {
        @Cleanup
        Connection connection = DriverManager.getConnection(
                JDBC_URL,
                "ether",
                "ether"
        );
        log.info("Connected to database {}.", JDBC_URL);

        Web3j web3j = Web3j.build(new HttpService(
                WEB3_URL,
                closeableHttpClient
        ));
        log.info("Connected to web3 {}.", WEB3_URL);

        long currentBlockNumber = web3j
                .ethBlockNumber()
                .send()
                .getBlockNumber()
                .longValue();
        log.debug("Latest block is {}", currentBlockNumber);

        ResultSet resultSet = connection.prepareStatement("SELECT max(id) FROM block")
                .executeQuery();
        long minBlockNumber = 0;
        if (resultSet.next()) {
            minBlockNumber = resultSet.getLong(1);
        }
        log.debug("Min block number is {}", minBlockNumber);

        if (minBlockNumber == currentBlockNumber) {
            log.info("No new blocks.");
            return;
        }

        long transactionIndex = 0;
        long blockNumber = minBlockNumber;
        for (; blockNumber <= currentBlockNumber; blockNumber++) {

            if (!terminated.get()) {
                semaphore.acquire();
                final long fBlockNumber = blockNumber;
                web3j.ethGetBlockByNumber(new DefaultBlockParameterNumber(blockNumber), true)
                        .sendAsync()
                        .thenAccept(block -> {
                            if (block == null) {
                                log.error("Empty block returned");
                                semaphore.release();
                                return;
                            }
                            block.setId(fBlockNumber);
                            resultQueue.add(block);
                            semaphore.release();
                        });
            }
            else {
                int restRequest = poolSize - semaphore.availablePermits();
                if (resultQueue.isEmpty() && restRequest == 0) {
                    break;
                }
                log.info("Terminated cycle, do not request more data. Wait till {} requests will be finished and {} block will be persisted.", restRequest, resultQueue.size());
            }

            while (!resultQueue.isEmpty()) {
                EthBlock ethBlock = resultQueue.remove();
                persister.persist(ethBlock.getId(), ethBlock.getBlock());
            }
        }

        while (!resultQueue.isEmpty()) {
            EthBlock ethBlock = resultQueue.remove();
            persister.persist(ethBlock.getId(), ethBlock.getBlock());
        }

        persister.flushBlock();
        persister.flushTransaction();
        persister.close();

        synchronized (waitExit) {
            waitExit.notifyAll();
        }
    }

    //    public static byte[] addressToBinary(String address) {
//        if (address.startsWith("0x")) {
//            address = address.substring(2);
//        }
//        int length = address.length() / 2;
//        byte[] result = new byte[length];
//        for (int i = length - 1; i >= 0; i--) {
//            byte b = (byte) (Character.digit(address.charAt(i * 2), 16) << 4);
//            b |= (byte) (Character.digit(address.charAt(i * 2 + 1), 16));
//            result[i] = b;
//        }
//        return result;
//    }
}
