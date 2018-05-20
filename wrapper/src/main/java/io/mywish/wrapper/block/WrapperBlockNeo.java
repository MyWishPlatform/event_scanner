package io.mywish.wrapper.block;

import com.glowstick.neocli4j.Block;
import io.mywish.wrapper.WrapperBlock;
import io.mywish.wrapper.transaction.WrapperTransactionNeo;

import java.util.stream.Collectors;

public class WrapperBlockNeo extends WrapperBlock {
    public WrapperBlockNeo(Block block) {
        super(
                block.getHash(),
                block.getNumber(),
                block.getTimestamp(),
                block.getTransactions().stream().map(WrapperTransactionNeo::new).collect(Collectors.toList())
        );
    }
}
