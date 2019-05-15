package io.mywish.binance.blockchain.services;

import com.binance.dex.api.client.domain.broadcast.MultiTransfer;
import com.binance.dex.api.client.domain.broadcast.Transaction;
import com.binance.dex.api.client.domain.broadcast.Transfer;
import com.binance.dex.api.client.domain.broadcast.TxType;
import io.mywish.binance.blockchain.model.WrapperOutputBinance;
import io.mywish.binance.blockchain.model.WrapperTransactionBinance;
import io.mywish.blockchain.WrapperOutput;
import io.mywish.blockchain.WrapperTransaction;
import io.mywish.blockchain.service.WrapperTransactionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class WrapperTransactionBinanceService implements WrapperTransactionService<Transaction> {
    @Override
    public WrapperTransaction build(Transaction transaction) {
        List<WrapperOutput> outputs = new ArrayList<>();
        TxType txType = transaction.getTxType();
        Object tx = transaction.getRealTx();
        if (txType.equals(TxType.TRANSFER)) {
            if (tx instanceof Transfer) {
                Transfer transfer = (Transfer) tx;
                outputs.add(new WrapperOutputBinance(
                        transaction,
                        transfer.getFromAddress(),
                        transfer.getToAddress(),
                        transfer.getCoin(),
                        transfer.getAmount()
                ));
            } else if (tx instanceof MultiTransfer) {
                MultiTransfer transfer = (MultiTransfer) tx;
                transfer.getOutputs()
                        .forEach(output -> output.getTokens()
                                .forEach(outputToken -> outputs.add(new WrapperOutputBinance(
                                        transaction,
                                        transfer.getFromAddress(),
                                        output.getAddress(),
                                        outputToken.getCoin(),
                                        outputToken.getAmount()
                                )))
                        );
            } else {
                log.error("Unknown transaction type {}.", transaction.getHash());
            }
        }

        return new WrapperTransactionBinance(
                transaction.getHash(),
                outputs,
                transaction
        );
    }
}
