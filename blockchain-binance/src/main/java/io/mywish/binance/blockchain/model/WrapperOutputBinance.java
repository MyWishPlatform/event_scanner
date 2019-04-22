package io.mywish.binance.blockchain.model;

import com.binance.dex.api.client.domain.broadcast.Transaction;
import io.mywish.blockchain.WrapperOutput;
import lombok.Getter;

import java.math.BigInteger;

@Getter
public class WrapperOutputBinance extends WrapperOutput {
    private final Transaction transaction;

    public WrapperOutputBinance(String parentTransaction, String address, BigInteger value, Transaction transaction) {
        super(parentTransaction, 0, address, value, new byte[0]);
        this.transaction = transaction;
    }
}
