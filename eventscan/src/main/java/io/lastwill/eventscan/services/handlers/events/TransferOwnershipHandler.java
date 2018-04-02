package io.lastwill.eventscan.services.handlers.events;

import io.lastwill.eventscan.events.contract.OwnershipTransferredEvent;
import io.lastwill.eventscan.messages.OwnershipTransferredNotify;
import io.lastwill.eventscan.repositories.ProductRepository;
import io.lastwill.eventscan.services.ExternalNotifier;
import io.mywish.scanner.model.NetworkType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TransferOwnershipHandler {
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private ExternalNotifier externalNotifier;

    public void handle(final NetworkType networkType, final OwnershipTransferredEvent event) {
        String tokenAddress = event.getAddress();
        String transferTo = event.getNewOwner();

        productRepository.findCrowdsaleByAddressAndTokenAddress(transferTo, tokenAddress, networkType)
                .forEach(productCrowdsale -> externalNotifier.send(
                        networkType,
                        new OwnershipTransferredNotify(
                                productCrowdsale.getTokenContract().getId(),
                                event.getTransactionReceipt().getTransactionHash(),
                                productCrowdsale.getCrowdsaleContract().getId()
                        )
                ));
    }
}
