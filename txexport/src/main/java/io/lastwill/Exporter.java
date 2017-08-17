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
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@Component
public class Exporter implements ApplicationRunner {

    @Value("${io.lastwill.jdbc-url}")
    private String JDBC_URL;
    @Value("${io.lastwill.web3-url}")
    private String WEB3_URL;

    @Autowired
    private CloseableHttpClient closeableHttpClient;

    private final AtomicBoolean terminated = new AtomicBoolean(false);
    private final Object waitExit = new Object();

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
    }

    private static BigDecimal toEther(BigInteger amount) {
        if (amount == null) {
            return new BigDecimal(0L);
        }
        return new BigDecimal(amount)
                .divide(BigDecimal.valueOf(1000000000000000000L), 18, RoundingMode.CEILING);
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

        @Cleanup
        PreparedStatement insertTransaction = connection.prepareStatement("INSERT INTO transaction (id, block_number, value, from_address, to_address, nonce, gas, gas_price) VALUES (decode(?, 'hex'), ?, ?, decode(?, 'hex'), decode(?, 'hex'), ?, ?, ?)");
        final int ID_ORDINAL = 1;
        final int BLOCK_NUMBER_ORDINAL = 2;
        final int VALUE_ORDINAL = 3;
        final int FROM_ORDINAL = 4;
        final int TO_ORDINAL = 5;
        final int NONCE_ORDINAL = 6;
        final int GAS_ORDINAL = 7;
        final int GAS_PRICE_ORDINAL = 8;

        PreparedStatement insertBlock = connection.prepareStatement("INSERT INTO block (id, timestamp, miner_address, difficulty) VALUES (?, ?, decode(?, 'hex'), ?)");
        final int TIMESTAMP_ORDINAL = 2;
        final int MINER_ORDINAL = 3;
        final int DIFFICULTY_ORDINAL = 4;

        long transactionIndex = 0;
        long blockNumber = minBlockNumber;
        for (; blockNumber <= currentBlockNumber; blockNumber++) {
            EthBlock.Block block = web3j.ethGetBlockByNumber(new DefaultBlockParameterNumber(blockNumber), true)
                    .send()
                    .getBlock();

            if (block == null) {
                log.warn("Skip block {}, null returned.", blockNumber);
                continue;
            }

            insertBlock.setLong(ID_ORDINAL, blockNumber);
            insertBlock.setInt(TIMESTAMP_ORDINAL, block.getTimestamp() == null ? 0 : block.getTimestamp().intValue());
            insertBlock.setString(MINER_ORDINAL, block.getMiner().substring(2));
            insertBlock.setLong(DIFFICULTY_ORDINAL, block.getDifficulty().longValue());
            insertBlock.addBatch();

            for (EthBlock.TransactionResult<EthBlock.TransactionObject> transaction : block.getTransactions()) {
                EthBlock.TransactionObject transactionObject = transaction.get();
                try {
                    String id = transactionObject.getHash();
                    String from = transactionObject.getFrom();
                    String to = transactionObject.getTo();
                    if (to == null) {
                        to = "0x0000000000000000000000000000000000000000";
                    }

                    insertTransaction.setString(ID_ORDINAL, id.substring(2));
                    insertTransaction.setBigDecimal(VALUE_ORDINAL, toEther(transactionObject.getValue()));
                    insertTransaction.setLong(BLOCK_NUMBER_ORDINAL, blockNumber);
                    insertTransaction.setString(FROM_ORDINAL, from.substring(2));
                    insertTransaction.setString(TO_ORDINAL, to.substring(2));
                    insertTransaction.setLong(NONCE_ORDINAL, transactionObject.getNonce().longValue());
                    insertTransaction.setBigDecimal(GAS_ORDINAL, toEther(transactionObject.getGas()));
                    insertTransaction.setBigDecimal(GAS_PRICE_ORDINAL, toEther(transactionObject.getGasPrice()));
                    insertTransaction.addBatch();
                    transactionIndex ++;
                }
                catch (Exception e) {
                    log.error("Skip transaction {}.", transactionObject.getHash(), e);
                }
            }

            if (transactionIndex % 100 == 0 && transactionIndex != 0) {
                try {
                    insertTransaction.executeBatch();
                }
                catch (Exception e) {
                    log.error("Error on saving transaction batch.", e);
                }
                log.debug("Block {}, transaction {}.", blockNumber, transactionIndex);
                if (transactionIndex % 10000 == 0 && !log.isDebugEnabled()) {
                    log.info("Block {}, transaction {}.", blockNumber, transactionIndex);
                }
            }

            if (blockNumber % 100 == 0 && blockNumber != 0) {
                try {
                    insertBlock.executeBatch();
                }
                catch (Exception e) {
                    log.error("Error on saving block batch.", e);
                }
                log.debug("Block {}, transaction {}.", blockNumber, transactionIndex);
            }

            if (terminated.get()) {
                log.info("Terminated cycle.");
                break;
            }
        }

        insertTransaction.executeBatch();
        insertBlock.executeBatch();
        log.info("Block {}, transaction {}.", blockNumber, transactionIndex);

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
