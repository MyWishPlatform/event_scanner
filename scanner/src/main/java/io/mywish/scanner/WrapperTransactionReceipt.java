package io.mywish.scanner;

import lombok.Getter;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.utils.Numeric;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

@Getter
public class WrapperTransactionReceipt {
    private final String transactionHash;
    private final String contractAddress;
    private final List<WrapperLog> logs;
    private final boolean success;

    public WrapperTransactionReceipt(String txHash, String contractAddress, boolean success) {
        this.transactionHash = txHash;
        this.contractAddress = contractAddress;
        this.logs = new ArrayList<>();
        this.success = success;
    }

    public WrapperTransactionReceipt(TransactionReceipt web3TxReceipt) {
        this(
                web3TxReceipt.getTransactionHash(),
                web3TxReceipt.getContractAddress(),
                (web3TxReceipt.getStatus().startsWith("0x") ? Numeric.decodeQuantity(web3TxReceipt.getStatus()) : new BigInteger(web3TxReceipt.getStatus()))
                        .compareTo(BigInteger.ZERO) != 0
        );
    }
}
