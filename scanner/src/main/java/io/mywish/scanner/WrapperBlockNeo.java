package io.mywish.scanner;

import com.glowstick.neocli4j.Block;

import java.util.stream.Collectors;

public class WrapperBlockNeo extends WrapperBlock {
    public WrapperBlockNeo(Block block) {
        super(
                block.getNumber(),
                block.getTimestamp(),
                block.getTransactions().stream().map(WrapperTransactionNeo::new).collect(Collectors.toList())
        );
    }
}
