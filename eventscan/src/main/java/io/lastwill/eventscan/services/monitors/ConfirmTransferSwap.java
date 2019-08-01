package io.lastwill.eventscan.services.monitors;

import io.lastwill.eventscan.events.model.contract.erc20.TransferEvent;
import io.lastwill.eventscan.events.model.wishbnbswap.TokensTransferredEvent;
import io.lastwill.eventscan.model.*;
import io.lastwill.eventscan.repositories.EthToBnbSwapEntryRepository;
import io.mywish.binance.blockchain.model.WrapperOutputBinance;
import io.mywish.blockchain.WrapperOutput;
import io.mywish.blockchain.WrapperTransaction;
import io.mywish.scanner.model.NewBlockEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class ConfirmTransferSwap {

    private final List<TokensTransferredEvent> transferredEvents = new ArrayList<>();

    @Autowired
    EthToBnbSwapEntryRepository repository;

    @EventListener(TokensTransferredEvent.class)
    public void onConfirm(final TokensTransferredEvent transferredEvent) {

        transferredEvents.add(transferredEvent);
    }

    @EventListener(NewBlockEvent.class)
    public void checkBlock(final NewBlockEvent newBlockEvent) {
        if (!repository.existsByTransferStatus(TransferStatus.WAIT_FOR_CONFIRM)
                || newBlockEvent.getNetworkType() != NetworkType.BINANCE_MAINNET) {
            return;
        }
        List<EthToBnbSwapEntry> entries = repository.findEthToBnbSwapEntriesByTransferStatus(TransferStatus.WAIT_FOR_CONFIRM);
        for (EthToBnbSwapEntry entry : entries) {
            boolean result = newBlockEvent.getTransactionsByAddress().entrySet()
                    .stream()
                    .filter(event -> event.getKey().equalsIgnoreCase(entry.getLinkEntry().getBnbAddress()))
                    .map(Map.Entry::getValue)
                    .flatMap(Collection::stream)
                    .anyMatch(transaction -> transaction.getHash().equalsIgnoreCase(entry.getBnbTxHash()));
            if(result) {
                entry.setTransferStatus(TransferStatus.OK);
                repository.save(entry);
            }
        }


//        transferredEvents.forEach(event -> {
//            newBlockEvent.getTransactionsByAddress()
//                    .entrySet()
//                    .stream()
//                    .filter(entry -> event.getEthEntry().getLinkEntry().getBnbAddress().equalsIgnoreCase(entry.getKey()))
//                    .map(Map.Entry::getValue)
//                    .flatMap(Collection::stream)
//                    .forEach(transaction -> transaction.getOutputs()
//                            .stream()
//                            .map(output -> ((WrapperOutputBinance) output))
//                            .filter(output -> event.getBnbSenderAddress().equalsIgnoreCase(output.getAddress()))
//                            .filter(output -> output.getFrom().equalsIgnoreCase(event.getBnbSenderAddress()))
//                            .forEach(e -> {
//                                event.getEthEntry().setTransferStatus(TransferStatus.OK);
//                                transferredEvents.remove(transferredEvents.size() - 1);
//                            }));
//
//        });

    }
}
