package io.mywish.wrapper.block;

import io.mywish.wrapper.WrapperBlock;
import io.mywish.wrapper.transaction.WrapperTransactionBtc;
import org.bitcoinj.core.Block;
import org.bitcoinj.core.NetworkParameters;

import java.util.stream.Collectors;

public class WrapperBlockBtc extends WrapperBlock {
    public WrapperBlockBtc(Block block, NetworkParameters networkParameters) {
        super(
                block.getHashAsString(),
                block.getNonce(),
                block.getTimeSeconds(),
                block.getTransactions().stream().map(tx -> new WrapperTransactionBtc(tx, networkParameters)).collect(Collectors.toList())
        );
    }
}
