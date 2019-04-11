package io.mywish.waves.blockchain.builders;

import com.wavesplatform.wavesj.Transaction;
import com.wavesplatform.wavesj.transactions.MassTransferTransaction;
import com.wavesplatform.wavesj.transactions.TransferTransaction;
import io.lastwill.eventscan.events.model.contract.waves.AssetTransferEvent;
import io.mywish.blockchain.ContractEvent;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Getter
@Component
public class AssetTransferWavesEventBuilder extends WavesEventBuilder<AssetTransferEvent> {
    @Override
    public List<ContractEvent> build(Transaction transaction) {
        List<ContractEvent> events = new ArrayList<>();

        if (transaction instanceof TransferTransaction) {
            TransferTransaction transfer = (TransferTransaction) transaction;
            events.add(new AssetTransferEvent(
                    transfer.getAssetId(),
                    transfer.getSenderPublicKey().getAddress(),
                    transfer.getRecipient(),
                    transfer.getAmount(),
                    transfer.getAttachment().getBytes()));
        } else if (transaction instanceof MassTransferTransaction) {
            MassTransferTransaction massTransfer = (MassTransferTransaction) transaction;
            massTransfer.getTransfers().forEach(transfer -> events.add(new AssetTransferEvent(
                    massTransfer.getAssetId(),
                    massTransfer.getSenderPublicKey().getAddress(),
                    transfer.getRecipient(),
                    transfer.getAmount(),
                    massTransfer.getAttachment().getBytes())));
        }

        return events;
    }
}