package io.mywish.waves.blockchain.model;

import com.wavesplatform.wavesj.Transaction;
import io.mywish.blockchain.WrapperOutput;
import lombok.Getter;

import java.math.BigInteger;

@Getter
public class WrapperOutputWaves extends WrapperOutput {
    private final Transaction transaction;

    public WrapperOutputWaves(String parentTransaction, String address, BigInteger value, Transaction transaction) {
        super(parentTransaction, 0, address, value, new byte[0]);
        this.transaction = transaction;
    }
}
