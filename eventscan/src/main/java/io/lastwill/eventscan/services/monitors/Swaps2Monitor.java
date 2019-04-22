package io.lastwill.eventscan.services.monitors;

import io.lastwill.eventscan.events.model.SwapsOrderCreatedEvent;
import io.lastwill.eventscan.events.model.contract.swaps2.CancelEvent;
import io.lastwill.eventscan.events.model.contract.swaps2.OrderCreatedEvent;
import io.lastwill.eventscan.events.model.contract.swaps2.SwapEvent;
import io.lastwill.eventscan.events.model.contract.swaps2.Swaps2BaseEvent;
import io.lastwill.eventscan.messages.swaps2.CancelledNotify;
import io.lastwill.eventscan.messages.swaps2.FinalizedNotify;
import io.lastwill.eventscan.messages.swaps2.OrderCreatedNotify;
import io.lastwill.eventscan.model.NetworkType;
import io.lastwill.eventscan.model.ProductSwaps2;
import io.lastwill.eventscan.repositories.ProductRepository;
import io.lastwill.eventscan.services.ExternalNotifier;
import io.lastwill.eventscan.services.TransactionProvider;
import io.mywish.scanner.model.NewBlockEvent;
import io.mywish.scanner.services.EventPublisher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Component
public class Swaps2Monitor {
    @Autowired
    private ExternalNotifier externalNotifier;

    @Autowired
    private EventPublisher eventPublisher;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private TransactionProvider transactionProvider;

    @Value("${io.lastwill.eventscan.eth.swaps2-address.mainnet}")
    private String swaps2AddressMainnet;

    @Value("${io.lastwill.eventscan.eth.swaps2-address.ropsten}")
    private String swaps2AddressRopsten;

    private Map<NetworkType, String> networkToSwapsAddresses = new HashMap<>();

    @PostConstruct
    protected void init() {
        networkToSwapsAddresses.put(NetworkType.ETHEREUM_MAINNET, swaps2AddressMainnet);
        networkToSwapsAddresses.put(NetworkType.ETHEREUM_ROPSTEN, swaps2AddressRopsten);
    }

    @EventListener
    private void onNewBlockEvent(NewBlockEvent event) {
        if (!networkToSwapsAddresses.keySet().contains(event.getNetworkType())) {
            return;
        }

        event.getTransactionsByAddress()
                .entrySet()
                .stream()
                .filter(entry -> Objects.equals(entry.getKey(), networkToSwapsAddresses.get(event.getNetworkType())))
                .map(Map.Entry::getValue)
                .flatMap(Collection::stream)
                .forEach(tx -> transactionProvider.getTransactionReceiptAsync(event.getNetworkType(), tx)
                        .thenAccept(receipt -> receipt.getLogs()
                                .stream()
                                .filter(contractEvent -> contractEvent instanceof Swaps2BaseEvent)
                                .map(contractEvent -> (Swaps2BaseEvent) contractEvent)
                                .filter(contractEvent -> productRepository
                                        .findByOrderId(contractEvent.getId(), event.getNetworkType()) != null)
                                .peek(contractEvent -> {
                                    if (contractEvent instanceof OrderCreatedEvent) {
                                        ProductSwaps2 swaps2 = productRepository.findByOrderId(
                                                contractEvent.getId(), event.getNetworkType());
                                        eventPublisher.publish(new SwapsOrderCreatedEvent(
                                                event.getNetworkType(),
                                                swaps2,
                                                tx));
                                    }
                                })
                                .map(contractEvent -> {
                                    if (contractEvent instanceof OrderCreatedEvent) {
                                        return new OrderCreatedNotify(
                                                tx.getHash(),
                                                receipt.isSuccess(),
                                                networkToSwapsAddresses.get(event.getNetworkType()),
                                                contractEvent.getId()
                                        );
                                    } else if (contractEvent instanceof CancelEvent) {
                                        return new CancelledNotify(
                                                tx.getHash(),
                                                receipt.isSuccess(),
                                                networkToSwapsAddresses.get(event.getNetworkType()),
                                                contractEvent.getId()
                                        );
                                    } else if (contractEvent instanceof SwapEvent) {
                                        return new FinalizedNotify(
                                                tx.getHash(),
                                                receipt.isSuccess(),
                                                networkToSwapsAddresses.get(event.getNetworkType()),
                                                contractEvent.getId()
                                        );
                                    }
                                    return null;
                                })
                                .filter(Objects::nonNull)
                                .forEach(swaps2Notify -> {
                                    externalNotifier.send(event.getNetworkType(), swaps2Notify);
                                })
                        )
                        .exceptionally(throwable -> {
                            log.error("Swaps2 event handling error.", throwable);
                            return null;
                        })
                );
    }
}
