package io.lastwill.eventscan.services.handlers;

import io.lastwill.eventscan.messages.MainPaymentNotify;
import io.lastwill.eventscan.messages.PaymentStatus;
import io.lastwill.eventscan.services.Btc2RskNetworkConverter;
import io.lastwill.eventscan.services.ExternalNotifier;
import io.lastwill.eventscan.model.NetworkType;
import io.mywish.scanner.model.NewBtcBlockEvent;
import lombok.extern.slf4j.Slf4j;
import org.bitcoinj.core.TransactionOutput;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.PayloadApplicationEvent;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Slf4j
@Component
public class BtcToMainEventHandler implements ApplicationListener<PayloadApplicationEvent> {
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

    @Override
    public void onApplicationEvent(PayloadApplicationEvent springEvent) {
        Object event = springEvent.getPayload();
        if (event instanceof NewBtcBlockEvent) handleBtcBlock((NewBtcBlockEvent) event);
    }

    private void handleBtcBlock(NewBtcBlockEvent event) {
        NetworkType sourceNetwork = event.getNetworkType();
        final String mainAddress = mainAddresses.get(sourceNetwork);
        NetworkType targetNetwork = btc2RskNetworkConverter.convert(sourceNetwork);
        Set<String> addresses = event.getAddressTransactionOutputs().keySet();
        if (!addresses.contains(mainAddress)) {
            return;
        }
        List<TransactionOutput> outputs = event.getAddressTransactionOutputs().get(mainAddress);
        for (int index = 0; index < outputs.size(); index++) {
            TransactionOutput output = outputs.get(index);
            if (output.getParentTransaction() == null) {
                continue;
            }
            externalNotifier.send(targetNetwork, new MainPaymentNotify(
                    BigInteger.valueOf(output.getValue().value),
                    PaymentStatus.COMMITTED,
                    output.getParentTransaction().getHashAsString(),
                    index
            ));
        }
    }
}
