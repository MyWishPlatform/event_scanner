package io.mywish.binance.blockchain.services;

import com.binance.dex.api.client.domain.broadcast.Transaction;
import com.binance.dex.api.client.domain.broadcast.TxType;
import com.fasterxml.jackson.databind.JsonNode;
import io.mywish.blockchain.WrapperOutput;
import io.mywish.blockchain.service.WrapperOutputService;
import io.mywish.binance.blockchain.model.WrapperOutputBinance;
import org.springframework.stereotype.Component;

import java.math.BigInteger;

@Component
public class WrapperOutputBinanceService implements WrapperOutputService<Transaction> {
    @Override
    public WrapperOutput build(Transaction transaction) {

        TxType txType = transaction.getTxType();

        String outputAddress;
        BigInteger value = BigInteger.ZERO;
        if (txType.equals(TxType.TRANSFER)) {
            transaction.getRealTx();
        }

        return new WrapperOutputBinance(
                transaction.getHash(),
                "",
                value,
                transaction
        );
    }
}
