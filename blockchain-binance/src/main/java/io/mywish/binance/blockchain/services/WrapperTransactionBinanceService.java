package io.mywish.binance.blockchain.services;

import com.binance.dex.api.client.domain.broadcast.Transaction;
import io.mywish.binance.blockchain.model.WrapperTransactionBinance;
import io.mywish.blockchain.WrapperTransaction;
import io.mywish.blockchain.service.WrapperTransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
public class WrapperTransactionBinanceService implements WrapperTransactionService<Transaction> {
    @Autowired
    private WrapperOutputBinanceService outputBuilder;

    @Override
    public WrapperTransaction build(Transaction transaction) {
        return new WrapperTransactionBinance(
                transaction.getHash(),
                Collections.singletonList(outputBuilder.build(transaction)),
                transaction
        );
    }
}
