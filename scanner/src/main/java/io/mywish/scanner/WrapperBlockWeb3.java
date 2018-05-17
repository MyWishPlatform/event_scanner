package io.mywish.scanner;

import org.web3j.protocol.core.methods.response.EthBlock;

import java.util.stream.Collectors;

public class WrapperBlockWeb3 extends WrapperBlock {
    public WrapperBlockWeb3(EthBlock.Block block) {
        super(
                block.getNumber().longValue(),
                block.getTimestamp().longValue(),
                block.getTransactions().stream().map(tx -> new WrapperTransactionWeb3(((EthBlock.TransactionObject) tx).get())).collect(Collectors.toList())
        );
    }
}
