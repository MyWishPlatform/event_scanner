package io.mywish.binance.blockchain.model;

import com.binance.dex.api.client.domain.broadcast.Transaction;
import io.mywish.blockchain.WrapperOutput;
import io.mywish.blockchain.WrapperTransaction;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class WrapperTransactionBinance extends WrapperTransaction {
    private final Transaction transaction;

    public WrapperTransactionBinance(String hash, List<WrapperOutput> outputs, Transaction transaction) {
        super(hash, new ArrayList<>(), outputs, false);
        this.transaction = transaction;
    }
}
