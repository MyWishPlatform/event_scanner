package io.lastwill.eventscan.services.monitors;

import com.glowstick.neocli4j.Transaction;
import io.lastwill.eventscan.events.ProductPaymentEvent;
import io.mywish.scanner.model.NetworkType;
import io.mywish.scanner.model.NewNeoBlockEvent;
import io.mywish.scanner.services.EventPublisher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class NeoPaymentMonitor {
    @Autowired
    private EventPublisher eventPublisher;

    private final String addressToWatch = "";

    @EventListener
    public void handleNeoBlock(NewNeoBlockEvent event) {
        if (event.getNetworkType() != NetworkType.NEO_MAINNET) return;
        event.getBlock().getTransactions().forEach(tx -> {
            if (tx.getType() == Transaction.Type.Invocation && tx.getOutputs().size() > 0) {
//                System.out.println(tx.getHash() + ": " + tx.getScript().length());
            }
        });
    }
}
