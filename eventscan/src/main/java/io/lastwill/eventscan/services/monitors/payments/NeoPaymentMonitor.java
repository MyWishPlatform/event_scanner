package io.lastwill.eventscan.services.monitors.payments;

import io.lastwill.eventscan.events.model.NeoPaymentEvent;
import io.lastwill.eventscan.model.CryptoCurrency;
import io.lastwill.eventscan.model.NetworkType;
import io.mywish.blockchain.WrapperOutput;
import io.mywish.scanner.model.NewBlockEvent;
import io.mywish.scanner.services.EventPublisher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class NeoPaymentMonitor {
    // TODO remove
    private final String addressToWatch = "AJFTKKCTVsxvXfctQuYeRqFGZhcQC99pKu";
    private final Map<String, CryptoCurrency> assets = new HashMap<String, CryptoCurrency>() {{
        put("0xc56f33fc6ecfcd0c225c4ab356fee59390af8560be0e930faebe74a6daff7c9b", CryptoCurrency.NEO);
        put("0x602c79718b16e442de58778e148d0b1084e3b2dffd5de6b7b16cee7969282de7", CryptoCurrency.GAS);
    }};
    @Autowired
    private EventPublisher eventPublisher;

    @EventListener
    private void handleNeoBlock(NewBlockEvent event) {
        if (event.getNetworkType() != NetworkType.NEO_MAINNET) {
            return;
        }

        event.getTransactionsByAddress()
                .get(addressToWatch)
                .forEach(wrapperTransaction -> {
                    for (WrapperOutput output : wrapperTransaction.getOutputs()) {
                        if (!addressToWatch.equalsIgnoreCase(output.getAddress())) {
                            return;
                        }
                        eventPublisher.publish(new NeoPaymentEvent(
                                event.getNetworkType(),
                                wrapperTransaction,
                                output.getAddress(),
                                output.getValue(),
                                true
                        ));

                    }
                });
    }
}
