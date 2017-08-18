package io.lastwill;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.web3j.protocol.core.methods.response.EthBlock;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@Slf4j
@Component
public class Persister {
    @Value("${io.lastwill.jdbc-url}")
    private String JDBC_URL;

    private Connection connection;

    private PreparedStatement insertTransaction;
    private final int ID_ORDINAL = 1;
    private final int BLOCK_NUMBER_ORDINAL = 2;
    private final int VALUE_ORDINAL = 3;
    private final int FROM_ORDINAL = 4;
    private final int TO_ORDINAL = 5;
    private final int NONCE_ORDINAL = 6;
    private final int GAS_ORDINAL = 7;
    private final int GAS_PRICE_ORDINAL = 8;

    private PreparedStatement insertBlock;
    private final int TIMESTAMP_ORDINAL = 2;
    private final int MINER_ORDINAL = 3;
    private final int DIFFICULTY_ORDINAL = 4;

    private long transactionIndex;


    @PostConstruct
    protected void init() throws SQLException {
        connection = DriverManager.getConnection(
                JDBC_URL,
                "ether",
                "ether"
        );
        log.info("Connected to database {}.", JDBC_URL);

        insertTransaction = connection.prepareStatement("INSERT INTO transaction (id, block_number, value, from_address, to_address, nonce, gas, gas_price) VALUES (decode(?, 'hex'), ?, ?, decode(?, 'hex'), decode(?, 'hex'), ?, ?, ?) ON CONFLICT DO NOTHING ");
        insertBlock = connection.prepareStatement("INSERT INTO block (id, timestamp, miner_address, difficulty) VALUES (?, ?, decode(?, 'hex'), ?) ON CONFLICT DO NOTHING ");
    }

    public void persist(long blockNumber, EthBlock.Block block) {
        try {
            innerPersist(blockNumber, block);
        }
        catch (Exception e) {
            log.error("Error on adding to batch block {}, skip it.", blockNumber, e);
        }

        if (blockNumber % 100 == 0 && blockNumber != 0) {
            flushBlock();
            log.debug("Flushed {} blocks.", blockNumber);
            if (blockNumber % 1000 == 0 && ! log.isDebugEnabled()) {
                log.info("Flushed {} blocks.", blockNumber);
            }
        }

        if (transactionIndex % 1000 == 0 && transactionIndex != 0) {
            flushTransaction();
            log.debug("Flushed {} transactions.", transactionIndex);
            if (transactionIndex % 1000 == 0 && ! log.isDebugEnabled()) {
                log.info("Flushed {} transactions.", transactionIndex);
            }
        }
    }

    public void flushBlock() {
        try {
            insertBlock.executeBatch();
        }
        catch (SQLException e) {
            log.error("Error on saving block batch.", e);
        }
    }

    public void flushTransaction() {
        try {
            insertTransaction.executeBatch();
        }
        catch (SQLException e) {
            log.error("Error on savging transaction batch.", e);
        }
    }

    private void innerPersist(long blockNumber, EthBlock.Block block) throws SQLException {
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
    }

    private static BigDecimal toEther(BigInteger amount) {
        if (amount == null) {
            return new BigDecimal(0L);
        }
        return new BigDecimal(amount)
                .divide(BigDecimal.valueOf(1000000000000000000L), 18, RoundingMode.CEILING);
    }

//    @Override
    public void close() throws Exception {
        if (insertTransaction != null) {
            flushTransaction();
            insertTransaction.close();
        }
        if (insertBlock != null) {
            flushBlock();
            insertBlock.close();
        }
        if (connection != null) {
            connection.close();
        }
    }


//    private ExecutorService executorService;
//
//    @Value("${io.lastwill.persister.pool-size:1}")
//    private int poolSize;
//
//    protected void init() {
//        executorService = Executors.newFixedThreadPool(poolSize);
//        executorService.
//    }
//
//    public void add(EthBlock.Block block) {
//
//    }
//
//    private class BlockSaver
}
