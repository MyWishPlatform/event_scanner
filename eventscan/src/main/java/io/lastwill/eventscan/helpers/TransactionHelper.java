package io.lastwill.eventscan.helpers;

import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.utils.Numeric;

import java.math.BigInteger;

public class TransactionHelper {
    public static boolean isSuccess(TransactionReceipt transactionReceipt) {
        if (transactionReceipt.getStatus() == null) {
            throw new IllegalStateException("Transaction receipt has no status field.");
        }
        BigInteger status = transactionReceipt.getStatus().startsWith("0x")
                ? Numeric.decodeQuantity(transactionReceipt.getStatus())
                : new BigInteger(transactionReceipt.getStatus());
        return status.compareTo(BigInteger.ZERO) != 0;
    }
}