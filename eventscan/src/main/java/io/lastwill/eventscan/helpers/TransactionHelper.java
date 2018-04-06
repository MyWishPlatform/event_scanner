package io.lastwill.eventscan.helpers;

import org.web3j.protocol.core.methods.response.Transaction;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.utils.Numeric;

import java.math.BigInteger;

public class TransactionHelper {
    private final static String ZERO_ADDRESS = "0x0000000000000000000000000000000000000000";

    public static boolean isSuccess(TransactionReceipt transactionReceipt) {
        if (transactionReceipt.getStatus() == null) {
            throw new IllegalStateException("Transaction receipt has no status field.");
        }
        BigInteger status = transactionReceipt.getStatus().startsWith("0x")
                ? Numeric.decodeQuantity(transactionReceipt.getStatus())
                : new BigInteger(transactionReceipt.getStatus());
        return status.compareTo(BigInteger.ZERO) != 0;
    }

    public static boolean isContractCreation(final Transaction transaction) {
        // ethereum condition || rsk condition
        return transaction.getTo() == null || ZERO_ADDRESS.equalsIgnoreCase(transaction.getTo());
    }
}