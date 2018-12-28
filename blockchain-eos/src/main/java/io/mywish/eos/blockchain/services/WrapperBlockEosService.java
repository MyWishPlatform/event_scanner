package io.mywish.eos.blockchain.services;

import io.mywish.blockchain.WrapperBlock;
import io.mywish.blockchain.WrapperTransaction;
import io.mywish.blockchain.service.WrapperBlockService;
import io.mywish.eoscli4j.model.response.BlockResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class WrapperBlockEosService implements WrapperBlockService<BlockResponse> {
    @Autowired
    private WrapperTransactionEosService transactionBuilder;

    @Override
    public WrapperBlock build(BlockResponse block) {
        String hash = block.getId();
        Long number = block.getBlockNum();
        Instant timestamp = block.getTimestamp().toInstant(ZoneOffset.UTC);
        List<WrapperTransaction> transactions = block
                .getTransactions()
                .stream()
                .map(transactionBuilder::build)
                .collect(Collectors.toList());
        return new WrapperBlock(hash, number, timestamp, transactions);
    }
}
