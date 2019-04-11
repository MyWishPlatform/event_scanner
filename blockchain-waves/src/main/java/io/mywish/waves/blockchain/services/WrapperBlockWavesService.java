package io.mywish.waves.blockchain.services;

import com.wavesplatform.wavesj.Block;
import io.mywish.blockchain.WrapperBlock;
import io.mywish.blockchain.WrapperTransaction;
import io.mywish.blockchain.service.WrapperBlockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class WrapperBlockWavesService implements WrapperBlockService<Block> {
    @Autowired
    WrapperTransactionWavesService transactionBuilder;

    @Override
    public WrapperBlock build(Block block) {
        String hash = block.getSignature();
        Long number = (long) block.getHeight();
        Instant timestamp = Instant.ofEpochMilli(block.getTimestamp());
        List<WrapperTransaction> transactions = block
                .getTransactions()
                .stream()
                .map(transactionBuilder::build)
                .collect(Collectors.toList());
        return new WrapperBlock(hash, number, timestamp, transactions);
    }
}
