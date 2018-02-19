package io.lastwill.eventscan.services.handlers.events;

import io.lastwill.eventscan.events.contract.OwnershipTransferredEvent;
import io.lastwill.eventscan.repositories.ProductRepository;
import io.lastwill.eventscan.services.ExternalNotifier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TransferOwnershipHandler {
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private ExternalNotifier externalNotifier;

    public void handle(OwnershipTransferredEvent event) {
        String tokenAddress = event.getAddress();
        String transferTo = event.getNewOwner();

        productRepository.findCrowdsaleByAddressAndTokenAddress(transferTo, tokenAddress)
                .forEach(productCrowdsale -> {
                    externalNotifier.sendOwnershipTransferredNotification(productCrowdsale.getTokenContract(), event.getTransactionReceipt().getTransactionHash());
                });
    }
}
