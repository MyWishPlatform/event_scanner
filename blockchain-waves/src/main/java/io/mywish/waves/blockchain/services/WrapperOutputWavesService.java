package io.mywish.waves.blockchain.services;

import com.wavesplatform.wavesj.Transaction;
import com.wavesplatform.wavesj.transactions.TransferTransaction;
import io.mywish.blockchain.WrapperOutput;
import io.mywish.blockchain.service.WrapperOutputService;
import io.mywish.waves.blockchain.model.WrapperOutputWaves;
import org.springframework.stereotype.Component;

import java.math.BigInteger;

@Component
public class WrapperOutputWavesService implements WrapperOutputService<Transaction> {
    @Override
    public WrapperOutput build(Transaction transaction) {
        BigInteger value;
        if (transaction instanceof TransferTransaction) {
            TransferTransaction transferTransaction = (TransferTransaction) transaction;
            value = BigInteger.valueOf(transferTransaction.getAmount());
        } else {
            value = BigInteger.ZERO;
        }

        String hash = null;
        try {
            hash = transaction.getId().getBase58String();
        } catch (NoSuchMethodError ignored) {
        }

        return new WrapperOutputWaves(
                hash,
                transaction.getSenderPublicKey().getAddress(),
                value,
                transaction
        );
    }
}
