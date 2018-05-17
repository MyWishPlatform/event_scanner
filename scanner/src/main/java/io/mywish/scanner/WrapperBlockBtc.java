package io.mywish.scanner;

import org.bitcoinj.core.Block;
import org.bitcoinj.core.NetworkParameters;

import java.util.stream.Collectors;

public class WrapperBlockBtc extends WrapperBlock {
    public WrapperBlockBtc(Block block, NetworkParameters networkParameters) {
        super(
                block.getNonce(),
                block.getTimeSeconds(),
                block.getTransactions().stream().map(tx -> new WrapperTransactionBtc(tx, networkParameters)).collect(Collectors.toList())
        );
    }
}
