package io.lastwill.eventscan.services.handlers.events;

import io.lastwill.eventscan.events.model.contract.OwnershipTransferredEvent;
import io.lastwill.eventscan.messages.OwnershipTransferredNotify;
import io.lastwill.eventscan.repositories.ProductRepository;
import io.lastwill.eventscan.services.ExternalNotifier;
import io.lastwill.eventscan.model.NetworkType;
import io.mywish.blockchain.WrapperTransactionReceipt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TransferOwnershipHandler {
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private ExternalNotifier externalNotifier;

    public void handle(final NetworkType networkType, final OwnershipTransferredEvent event, final WrapperTransactionReceipt transactionReceipt) {
        String tokenAddress = event.getAddress();
        String transferTo = event.getNewOwner();

        productRepository.findCrowdsaleByAddressAndTokenAddress(transferTo, tokenAddress, networkType)
                .forEach(productCrowdsale -> externalNotifier.send(
                        networkType,
                        new OwnershipTransferredNotify(
                                productCrowdsale.getTokenContract().getId(),
                                transactionReceipt.getTransactionHash(),
                                productCrowdsale.getCrowdsaleContract().getId()
                        )
                ));
    }
}
