package io.mywish.wrapper.transaction;

import io.mywish.wrapper.WrapperOutput;
import io.mywish.wrapper.WrapperTransaction;
import org.web3j.protocol.core.methods.response.Transaction;

import java.math.BigInteger;
import java.util.ArrayList;

public class WrapperTransactionWeb3 extends WrapperTransaction {
    private final static String ZERO_ADDRESS = "0x0000000000000000000000000000000000000000";

    public WrapperTransactionWeb3(Transaction transaction) {
        super(
                transaction.getHash(),
                new ArrayList<String>() {{
                    add(transaction.getFrom());
                }},
                new ArrayList<WrapperOutput>() {{
                    add(new WrapperOutput(transaction.getHash(), 0, transaction.getTo(), "0".equals(transaction.getValueRaw()) ? BigInteger.ZERO : transaction.getValue()));
                }},
                transaction.getTo() == null || ZERO_ADDRESS.equalsIgnoreCase(transaction.getTo())
        );
    }
}
