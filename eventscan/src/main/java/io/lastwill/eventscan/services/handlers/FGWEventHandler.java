package io.lastwill.eventscan.services.handlers;

import io.lastwill.eventscan.events.FGWBalanceChangedEvent;
import io.lastwill.eventscan.messages.FgwPaymentIncomeNotify;
import io.lastwill.eventscan.messages.FgwPaymentOutcomeNotify;
import io.lastwill.eventscan.messages.PaymentStatus;
import io.lastwill.eventscan.services.ExternalNotifier;
import io.lastwill.eventscan.model.NetworkType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.math.BigInteger;

@Slf4j
@Component
public class FGWEventHandler {
    @Autowired
    private ExternalNotifier externalNotifier;

    @EventListener
    private void handleFgwEvent(FGWBalanceChangedEvent event) {
        NetworkType targetNetwork = convert(event.getNetworkType());
        int diff = event.getDelta().compareTo(BigInteger.ZERO);
        if (diff > 0) {
            externalNotifier.send(targetNetwork, new FgwPaymentIncomeNotify(
                    event.getDelta(),
                    PaymentStatus.COMMITTED,
                    null,
                    event.isSuccess(),
                    event.getActualBalance()
            ));
        }
        else if (diff < 0) {
            externalNotifier.send(targetNetwork, new FgwPaymentOutcomeNotify(
                    event.getDelta().negate(),
                    PaymentStatus.COMMITTED,
                    null,
                    event.isSuccess(),
                    event.getActualBalance()
            ));
        }
        else {
            log.error("Wrong diff value! Fix the code!", new Exception());
        }
    }

    private NetworkType convert(NetworkType networkType) {
        switch (networkType) {
            case RSK_MAINNET:
                return NetworkType.RSK_FEDERATION_MAINNET;
            case RSK_TESTNET:
                return NetworkType.RSK_FEDERATION_TESTNET;
            default:
                throw new UnsupportedOperationException("Network " + networkType + " is not supported.");
        }
    }
}
