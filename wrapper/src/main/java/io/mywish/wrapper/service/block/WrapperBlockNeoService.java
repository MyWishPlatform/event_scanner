package io.mywish.wrapper.service.block;

import io.mywish.neocli4j.Block;
import io.mywish.wrapper.WrapperBlock;
import io.mywish.wrapper.WrapperTransaction;
import io.mywish.wrapper.service.WrapperBlockService;
import io.mywish.wrapper.service.transaction.WrapperTransactionNeoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class WrapperBlockNeoService implements WrapperBlockService<Block> {
    @Autowired
    WrapperTransactionNeoService transactionBuilder;

    @Override
    public WrapperBlock build(Block block) {
        String hash = block.getHash();
        Long number = block.getNumber();
        Long timestamp = block.getTimestamp();
        List<WrapperTransaction> transactions = block.getTransactions().stream().map(transactionBuilder::build).collect(Collectors.toList());
        return new WrapperBlock(hash, number, timestamp, transactions);
    }
}
