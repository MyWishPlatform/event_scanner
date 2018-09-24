package io.mywish.btc.blockchain.helper;

import lombok.extern.slf4j.Slf4j;
import org.bitcoinj.core.*;
import org.springframework.stereotype.Component;

import javax.xml.bind.DatatypeConverter;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class BtcBlockParser {
    private final static long LONG_MASK = 0xffffffffL;
    private final static long MAX_TRANSACTIONS_COUNT = 0x7fff;
    // must be less than 2^31
    private final static long MAX_SCRIPT_SIZE = 10000;
    private final static long MAX_COINBASE_SCRIPT_SIZE = 100;

    public Block parse(NetworkParameters parameters, String hex) {
        byte[] blockBytes = DatatypeConverter.parseHexBinary(hex);
        ByteBuffer buffer = ByteBuffer.wrap(blockBytes)
                .order(ByteOrder.LITTLE_ENDIAN);
        int version = buffer.getInt();
        byte[] prevBlockHashBytes = new byte[32];
        buffer.get(prevBlockHashBytes);
        byte[] merkleRootBytes = new byte[32];
        buffer.get(merkleRootBytes);
        long timestamp = buffer.getInt() & LONG_MASK;
        long difficulty = buffer.getInt() & LONG_MASK;
        long nonce = buffer.getInt() & LONG_MASK;
        long txCount = readVarInt(buffer);
        List<Transaction> transactions = readTransactions(parameters, txCount, buffer);
        return new Block(
                parameters,
                version,
                Sha256Hash.wrapReversed(prevBlockHashBytes),
                Sha256Hash.wrapReversed(merkleRootBytes),
                timestamp,
                difficulty,
                nonce,
                transactions
        );
    }

    private static List<Transaction> readTransactions(NetworkParameters parameters, long txCount, ByteBuffer buffer) {
        if (txCount > MAX_TRANSACTIONS_COUNT) {
            throw new ArrayIndexOutOfBoundsException("Transactions count is too big: " + txCount + " > " + MAX_TRANSACTIONS_COUNT);
        }
        List<Transaction> result = new ArrayList<>((int) txCount);
        for (int i = 0; i < txCount; i++) {
            Transaction transaction = readTransaction(parameters, buffer, i == 0);
            if (transaction == null) {
                log.warn("Skip transaction {}.", i);
                continue;
            }
            result.add(transaction);
        }
        return result;
    }

    private static TransactionInput readInput(NetworkParameters parameters, ByteBuffer buffer, boolean isCoinbase) {
        TransactionOutPoint outPoint = readOutPoint(parameters, buffer);
        long scriptSize = readVarInt(buffer);
        boolean skipScript = false;
        if (scriptSize > MAX_SCRIPT_SIZE) {
            log.warn("Script size is too big: {} > {}.", scriptSize, MAX_SCRIPT_SIZE);
            skipScript = true;
//            throw new ArrayIndexOutOfBoundsException("Script size is too big: " + scriptSize + " > " + MAX_SCRIPT_SIZE);
        }
        byte[] scriptBytes = new byte[skipScript ? 0 : (int) scriptSize];
        if (skipScript) {
            buffer.position(buffer.position() + (int) scriptSize);
        }
        else {
            buffer.get(scriptBytes);
        }
        long sequence = buffer.getInt() & LONG_MASK;
        TransactionInput input = isCoinbase
                ? new TransactionInput(parameters, null, scriptBytes)
                : new TransactionInput(parameters, null, scriptBytes, outPoint);
        input.setSequenceNumber(sequence);
        return skipScript ? null : input;
    }


    private static Transaction readTransaction(NetworkParameters parameters, ByteBuffer buffer, boolean isCoinbase) {
        Transaction transaction = new Transaction(parameters);

        long txVersion = buffer.getInt() & LONG_MASK;
        transaction.setVersion((int) txVersion);
        long inCount = readVarInt(buffer);
        boolean hasWitness = false;
        if (inCount == 0) {
            hasWitness = (buffer.get() & 0xff) == 1;
            inCount = readVarInt(buffer);
        }
        if (inCount > MAX_TRANSACTIONS_COUNT) {
            throw new ArrayIndexOutOfBoundsException("Inputs count is too big: " + inCount + " > " + MAX_TRANSACTIONS_COUNT);
        }

        boolean skipTransaction = false;
        for (int i = 0; i < inCount; i ++) {
            TransactionInput transactionInput = readInput(parameters, buffer, isCoinbase && i == 0);
            if (transactionInput == null) {
                skipTransaction = true;
                continue;
            }
            transaction.addInput(transactionInput);
        }

        long outCount = readVarInt(buffer);
        if (outCount > MAX_TRANSACTIONS_COUNT) {
            throw new ArrayIndexOutOfBoundsException("Outputs count is too big: " + outCount + " > " + MAX_TRANSACTIONS_COUNT);
        }

        for (int i = 0; i < outCount; i ++) {
            TransactionOutput transactionOutput = readOutput(parameters, buffer);
            if (transactionOutput == null) {
                skipTransaction = true;
                continue;
            }
            transaction.addOutput(transactionOutput);
        }

        if (hasWitness) {
            for (int i = 0; i < inCount; i ++) {
                byte[][] witnesses = readWitnesses(parameters, buffer);
            }
        }

        long lockTime = buffer.getInt() & LONG_MASK;
        transaction.setLockTime(lockTime);

        return skipTransaction ? null : transaction;
    }

    private static byte[][] readWitnesses(NetworkParameters parameters, ByteBuffer buffer) {
        long witnessesCount = readVarInt(buffer);
        if (witnessesCount > MAX_TRANSACTIONS_COUNT) {
            throw new ArrayIndexOutOfBoundsException("Witnesses count is too big: " + witnessesCount + " > " + MAX_TRANSACTIONS_COUNT);
        }
        byte[][] witnessesData = new byte[(int) witnessesCount][];
        for (int i = 0; i < witnessesCount; i ++) {
            long witnessSize = readVarInt(buffer);
            if (witnessSize > MAX_SCRIPT_SIZE) {
                log.warn("Witness size is too big: {} > {}.", witnessSize, MAX_SCRIPT_SIZE);
                buffer.position(buffer.position() + (int) witnessSize);
                continue;
//                throw new ArrayIndexOutOfBoundsException("Witness size is too big: " + witnessSize + " > " + MAX_SCRIPT_SIZE);
            }
            witnessesData[i] = new byte[(int) witnessSize];
            buffer.get(witnessesData[i]);
        }

        return witnessesData;
    }

    private static TransactionOutput readOutput(NetworkParameters parameters, ByteBuffer buffer) {
        long value = buffer.getLong();
        long scriptSize = readVarInt(buffer);
        if (scriptSize > MAX_SCRIPT_SIZE) {
            log.warn("Output script size is too big: {} > {}.", scriptSize, MAX_SCRIPT_SIZE);
            buffer.position(buffer.position() + (int) scriptSize);
            return null;
//            throw new ArrayIndexOutOfBoundsException("Script size is too big: " + scriptSize + " > " + MAX_SCRIPT_SIZE);
        }
        byte[] scriptBytes = new byte[(int) scriptSize];
        buffer.get(scriptBytes);
        return new TransactionOutput(parameters, null, Coin.valueOf(value), scriptBytes);
    }

    private static TransactionOutPoint readOutPoint(NetworkParameters parameters, ByteBuffer buffer) {
        byte[] hash = new byte[32];
        buffer.get(hash);
        long index = buffer.getInt() & LONG_MASK;
        return new TransactionOutPoint(parameters, index, Sha256Hash.wrapReversed(hash));
    }

    private static long readVarInt(ByteBuffer buffer) {
        int b = buffer.get() & 0xFF;
        if (b < 0xFD) {
            return b;
        }
        else if (b == 0xFD) {
            return buffer.getShort() & 0xffff;
        }
        else if (b == 0xFE) {
            return buffer.getInt() & LONG_MASK;
        }
        else {
            return buffer.getLong();
        }
    }
}
