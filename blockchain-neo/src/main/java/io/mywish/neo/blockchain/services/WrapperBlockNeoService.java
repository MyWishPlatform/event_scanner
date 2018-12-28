package io.mywish.neo.blockchain.services;

import io.mywish.neocli4j.Block;
import io.mywish.blockchain.WrapperBlock;
import io.mywish.blockchain.WrapperTransaction;
import io.mywish.blockchain.service.WrapperBlockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Instant;
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
        Instant timestamp = Instant.ofEpochSecond(block.getTimestamp());
        List<WrapperTransaction> transactions = block
                .getTransactions()
                .stream()
                .map(transactionBuilder::build)
                .collect(Collectors.toList());
        return new WrapperBlock(hash, number, timestamp, transactions);
    }
}
