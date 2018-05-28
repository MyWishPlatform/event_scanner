package io.lastwill.eventscan.services.monitors;

import io.mywish.neocli4j.Transaction;
import io.lastwill.eventscan.events.NeoPaymentEvent;
import io.lastwill.eventscan.model.CryptoCurrency;
import io.lastwill.eventscan.model.NetworkType;
import io.mywish.scanner.model.NewBlockEvent;
import io.mywish.scanner.services.EventPublisher;
import io.mywish.wrapper.transaction.WrapperTransactionNeo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class NeoPaymentMonitor {
    @Autowired
    private EventPublisher eventPublisher;

    // TODO remove
    private final String addressToWatch = "AJFTKKCTVsxvXfctQuYeRqFGZhcQC99pKu";

    private final Map<String, CryptoCurrency> assets = new HashMap<String, CryptoCurrency>() {{
            put("0xc56f33fc6ecfcd0c225c4ab356fee59390af8560be0e930faebe74a6daff7c9b", CryptoCurrency.NEO);
            put("0x602c79718b16e442de58778e148d0b1084e3b2dffd5de6b7b16cee7969282de7", CryptoCurrency.GAS);
    }};

    @EventListener
    private void handleNeoBlock(NewBlockEvent event) {
        if (event.getNetworkType() != NetworkType.NEO_MAINNET
                || event.getNetworkType() != NetworkType.NEO_TESTNET) {
            return;
        }
        event.getBlock().getTransactions().forEach(atx -> {
            WrapperTransactionNeo tx = (WrapperTransactionNeo) atx;
            if (tx.getType() == Transaction.Type.InvocationTransaction) {
//                System.out.println(tx.getHash());
            }
            if (tx.getType() == Transaction.Type.ContractTransaction) {
                tx.getOutputs().forEach(output -> {
                    if (output.getAddress().equals(addressToWatch)) {
                        eventPublisher.publish(new NeoPaymentEvent(
                                event.getNetworkType(),
                                tx,
                                output.getAddress(),
                                output.getValue(),
                                true
                        ));
                    }
                });
            }
        });
    }
}
