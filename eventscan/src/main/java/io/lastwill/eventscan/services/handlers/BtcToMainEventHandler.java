package io.lastwill.eventscan.services.handlers;

import io.lastwill.eventscan.messages.MainPaymentNotify;
import io.lastwill.eventscan.messages.PaymentStatus;
import io.lastwill.eventscan.services.Btc2RskNetworkConverter;
import io.lastwill.eventscan.services.ExternalNotifier;
import io.lastwill.eventscan.model.NetworkType;
import io.mywish.scanner.model.NewBlockEvent;
import io.mywish.blockchain.WrapperOutput;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Slf4j
@Component
public class BtcToMainEventHandler {
    @Autowired
    private ExternalNotifier externalNotifier;
    @Autowired
    private Btc2RskNetworkConverter btc2RskNetworkConverter;

    @Value("${io.lastwill.eventscan.rsk.btc-federation-gatewat-testnet-address}")
    private String btcMainTestnetAddress;
    @Value("${io.lastwill.eventscan.rsk.btc-federation-gatewat-mainnet-address}")
    private String btcMainMainnetAddress;

    private final Map<NetworkType, String> mainAddresses = new HashMap<>();

    @PostConstruct
    protected void init() {
        mainAddresses.put(NetworkType.BTC_MAINNET, btcMainMainnetAddress);
        mainAddresses.put(NetworkType.BTC_TESTNET_3, btcMainTestnetAddress);
    }

    @EventListener
    private void handleBtcBlock(NewBlockEvent event) {
        if (event.getNetworkType() != NetworkType.BTC_MAINNET ||
                event.getNetworkType() != NetworkType.BTC_TESTNET_3) {
            return;
        }
        NetworkType sourceNetwork = event.getNetworkType();
        final String mainAddress = mainAddresses.get(sourceNetwork);
        NetworkType targetNetwork = btc2RskNetworkConverter.convert(sourceNetwork);
        Set<String> addresses = event.getTransactionsByAddress().keySet();
        if (!addresses.contains(mainAddress)) {
            return;
        }
        event.getTransactionsByAddress().get(mainAddress).forEach(tx -> {
            for (int index = 0; index < tx.getOutputs().size(); index++) {
                WrapperOutput output = tx.getOutputs().get(index);
                if (output.getParentTransaction() == null) {
                    continue;
                }
                externalNotifier.send(targetNetwork, new MainPaymentNotify(
                        output.getValue(),
                        PaymentStatus.COMMITTED,
                        output.getParentTransaction(),
                        index
                ));
            }
        });
    }
}
