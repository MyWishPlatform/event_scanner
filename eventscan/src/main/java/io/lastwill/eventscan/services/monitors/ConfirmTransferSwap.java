package io.lastwill.eventscan.services.monitors;

import io.lastwill.eventscan.model.*;
import io.lastwill.eventscan.repositories.EthToBnbSwapEntryRepository;
import io.mywish.scanner.model.NewBlockEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@Component
public class ConfirmTransferSwap {

    @Autowired
    EthToBnbSwapEntryRepository repository;


    @EventListener(NewBlockEvent.class)
    public void checkBlock(final NewBlockEvent newBlockEvent) {
        if (!repository.existsByTransferStatus(TransferStatus.WAIT_FOR_CONFIRM)
                || newBlockEvent.getNetworkType() != NetworkType.BINANCE_MAINNET) {
            return;
        }

        List<EthToBnbSwapEntry> entries = repository.findEthToBnbSwapEntriesByTransferStatus(TransferStatus.WAIT_FOR_CONFIRM);

        for (EthToBnbSwapEntry entry : entries) {
            if (isCompleted(newBlockEvent, entry)) {
                confirmTransferStatus(entry);
            }
        }
    }

    private boolean isCompleted(NewBlockEvent newBlockEvent, EthToBnbSwapEntry entry) {
        return newBlockEvent.getTransactionsByAddress().entrySet()
                .stream()
                .filter(event -> event.getKey().equalsIgnoreCase(entry.getLinkEntry().getBnbAddress()))
                .map(Map.Entry::getValue)
                .flatMap(Collection::stream)
                .anyMatch(transaction -> transaction.getHash().equalsIgnoreCase(entry.getBnbTxHash()));
    }

    private void confirmTransferStatus(EthToBnbSwapEntry entry) {
        entry.setTransferStatus(TransferStatus.OK);
        repository.save(entry);
    }
}
