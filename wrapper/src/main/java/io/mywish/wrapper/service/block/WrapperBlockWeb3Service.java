package io.mywish.wrapper.service.block;

import io.mywish.wrapper.WrapperBlock;
import io.mywish.wrapper.WrapperTransaction;
import io.mywish.wrapper.service.WrapperBlockService;
import io.mywish.wrapper.service.transaction.WrapperTransactionWeb3Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.web3j.protocol.core.methods.response.EthBlock;

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
        Long timestamp = block.getTimestamp().longValue();
        List<WrapperTransaction> transactions = block.getTransactions().stream().map(tx ->
                transactionBuilder.build(((EthBlock.TransactionObject) tx).get())
        ).collect(Collectors.toList());
        return new WrapperBlock(hash, number, timestamp, transactions);
    }
}
