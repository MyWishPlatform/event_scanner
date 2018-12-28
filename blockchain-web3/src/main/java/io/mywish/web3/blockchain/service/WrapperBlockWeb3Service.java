package io.mywish.web3.blockchain.service;

import io.mywish.blockchain.WrapperBlock;
import io.mywish.blockchain.WrapperTransaction;
import io.mywish.blockchain.service.WrapperBlockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.web3j.protocol.core.methods.response.EthBlock;
import org.web3j.protocol.core.methods.response.Transaction;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class WrapperBlockWeb3Service implements WrapperBlockService<EthBlock.Block> {
    @Autowired
    WrapperTransactionWeb3Service transactionBuilder;

    @Override
    public WrapperBlock build(EthBlock.Block block) {
        String hash = block.getHash();
        Long number = block.getNumber().longValue();
        Instant timestamp = Instant.ofEpochSecond(block.getTimestamp().longValue());
        List<WrapperTransaction> transactions = block
                .getTransactions()
                .stream()
                .map(tx -> {
                    Transaction transaction = ((EthBlock.TransactionObject) tx).get();
                    return transactionBuilder.build(transaction);
                }).collect(Collectors.toList());
        return new WrapperBlock(hash, number, timestamp, transactions);
    }
}
