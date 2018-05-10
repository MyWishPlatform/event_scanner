package io.lastwill.eventscan.services.monitors;

import com.glowstick.neocli4j.Transaction;
import io.lastwill.eventscan.events.NeoPaymentEvent;
import io.lastwill.eventscan.events.UserPaymentEvent;
import io.lastwill.eventscan.model.CryptoCurrency;
import io.mywish.scanner.model.NetworkType;
import io.mywish.scanner.model.NewNeoBlockEvent;
import io.mywish.scanner.services.EventPublisher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.math.BigInteger;

@Slf4j
@Component
public class NeoPaymentMonitor {
    @Autowired
    private EventPublisher eventPublisher;

    private final String addressToWatch = "AJFTKKCTVsxvXfctQuYeRqFGZhcQC99pKu";

    // TODO: remove
    private static BigInteger toBigInteger(Double value, CryptoCurrency asset) {
        if (asset == CryptoCurrency.NEO) return BigInteger.valueOf(Math.round(value));
        if (asset == CryptoCurrency.GAS) return BigInteger.valueOf(Math.round(value * Math.pow(10, 8)));
        return null;
    }

    // TODO: remove
    private static CryptoCurrency assetToCryptocurrency(String asset) {
        if ("0xc56f33fc6ecfcd0c225c4ab356fee59390af8560be0e930faebe74a6daff7c9b".equals(asset)) return CryptoCurrency.NEO;
        if ("0x602c79718b16e442de58778e148d0b1084e3b2dffd5de6b7b16cee7969282de7".equals(asset)) return CryptoCurrency.GAS;
        return null;
    }

    @EventListener
    public void handleNeoBlock(NewNeoBlockEvent event) {
        if (event.getNetworkType() != NetworkType.NEO_MAINNET) return;
        event.getBlock().getTransactions().forEach(tx -> {
            if (tx.getType() == Transaction.Type.Contract) {
                tx.getOutputs().forEach(output -> {
                    if (output.getAddress().equals(addressToWatch)) {
                        CryptoCurrency asset = assetToCryptocurrency(output.getAsset());
                        eventPublisher.publish(new NeoPaymentEvent(
                                event.getNetworkType(),
                                output.getAddress(),
                                toBigInteger(output.getValue(), asset),
                                asset,
                                true
                        ));
                    }
                });
            }
        });
    }
}
