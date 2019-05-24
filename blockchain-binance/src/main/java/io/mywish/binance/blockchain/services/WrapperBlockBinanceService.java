package io.mywish.binance.blockchain.services;

import io.mywish.binance.blockchain.model.BinanceBlock;
import io.mywish.blockchain.WrapperBlock;
import io.mywish.blockchain.WrapperTransaction;
import io.mywish.blockchain.service.WrapperBlockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class WrapperBlockBinanceService implements WrapperBlockService<BinanceBlock> {
    @Autowired
    WrapperTransactionBinanceService transactionBuilder;

    @Override
    public WrapperBlock build(BinanceBlock block) {
        String hash = block.getBlockMeta().getBlockId().getHash();
        Long number = block.getBlockMeta().getHeader().getHeight();
        Instant timestamp = block.getBlockMeta().getHeader().getTime().toInstant();
        List<WrapperTransaction> transactions = block
                .getTransactions()
                .stream()
                .map(transactionBuilder::build)
                .collect(Collectors.toList());
        return new WrapperBlock(hash, number, timestamp, transactions);
    }
}
