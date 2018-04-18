package io.mywish.bot.integration.services;

import io.lastwill.eventscan.events.ContractCreatedEvent;
import io.lastwill.eventscan.events.FGWBalanceChangedEvent;
import io.lastwill.eventscan.events.ProductPaymentEvent;
import io.lastwill.eventscan.events.UserPaymentEvent;
import io.lastwill.eventscan.events.utility.NetworkStuckEvent;
import io.lastwill.eventscan.model.*;
import io.mywish.bot.service.MyWishBot;
import io.mywish.scanner.model.NetworkType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class BotIntegration {
    @Autowired
    private MyWishBot bot;

    @Value("${io.lastwill.eventscan.contract.crowdsale-address}")
    private String crowdsaleAddress;

    @Autowired
    protected BlockchainExplorerProvider explorerProvider;

    private final Map<NetworkType, String> networkName = new HashMap<NetworkType, String>() {{
        put(NetworkType.ETHEREUM_MAINNET, "ETH");
        put(NetworkType.ETHEREUM_ROPSTEN, "tETH");
        put(NetworkType.RSK_MAINNET, "RSK");
        put(NetworkType.RSK_TESTNET, "tRSK");
        put(NetworkType.BTC_TESTNET_3, "tBTC");
        put(NetworkType.BTC_MAINNET, "BTC");
    }};

    private final String defaultNetwork = "unknown";

    @EventListener
    public void onContractCrated(final ContractCreatedEvent contractCreatedEvent) {
        final Contract contract = contractCreatedEvent.getContract();
        final Product product = contract.getProduct();
        final String type = ProductStatistics.PRODUCT_TYPES.get(product.getContractType());
        final String network = networkName.getOrDefault(product.getNetwork().getType(), defaultNetwork);
        final String txLink = explorerProvider.getOrStub(product.getNetwork().getType())
                .buildToTransaction(contractCreatedEvent.getTransaction().getHash());
        final String addressLink = explorerProvider.getOrStub(product.getNetwork().getType())
                .buildToAddress(contract.getAddress());

        if (contractCreatedEvent.isSuccess()) {
            bot.onContract(network, product.getId(), type, contract.getId(), toCurrency(CryptoCurrency.ETH, product.getCost()), contract.getAddress(), addressLink);
        }
        else {
            bot.onContractFailed(network, product.getId(), type, contract.getId(), txLink);
        }
    }

    @EventListener
    public void onOwnerBalanceChanged(final UserPaymentEvent event) {
        final UserProfile userProfile = event.getUserProfile();
        final String network = networkName.getOrDefault(event.getNetworkType(), defaultNetwork);
        final String txLink = explorerProvider.getOrStub(event.getNetworkType())
                .buildToTransaction(event.getTransaction().getHash());
        bot.onBalance(
                network,
                userProfile.getUser().getId(),
                toCurrency(event.getCurrency(), event.getAmount()),
                txLink
        );
    }

    @EventListener
    public void onRskFGWBalanceChanged(final FGWBalanceChangedEvent event) {
        final String network = networkName.getOrDefault(event.getNetworkType(), defaultNetwork);
        final String link = explorerProvider.getOrStub(event.getNetworkType())
                .buildToAddress(event.getAddress());
        final String blockLink = explorerProvider.getOrStub(event.getNetworkType())
                .buildToBlock(event.getBlockNo());
        bot.onFGWBalanceChanged(
                network,
                toCurrency(event.getCurrency(), event.getDelta()),
                toCurrency(event.getCurrency(), event.getActualBalance()),
                link,
                event.getBlockNo(),
                blockLink
        );
    }

    @EventListener
    public void onBtcPaymentChanged(final ProductPaymentEvent event) {
        final String network = networkName.getOrDefault(event.getNetworkType(), defaultNetwork);
        final Product product = event.getProduct();
        final String type = ProductStatistics.PRODUCT_TYPES.get(product.getContractType());
        final String txLink = explorerProvider.getOrStub(event.getNetworkType())
                .buildToTransaction(event.getTransactionOutput().getParentTransaction().getHashAsString());
        bot.onBtcPayment(
                network,
                type,
                product.getId(),
                toCurrency(event.getCurrency(), event.getAmount()),
                txLink
        );
    }

    @EventListener
    public void onNetworkStuck(final NetworkStuckEvent event) {
        final String network = networkName.getOrDefault(event.getNetworkType(), defaultNetwork);
        ZonedDateTime lastBlock = event.getLastTimestamp().atZone(ZoneId.of("Europe/Moscow"));
        final String blockLink = explorerProvider.getOrStub(event.getNetworkType())
                .buildToBlock(event.getLastBlockNo());
        bot.sendToAll(
                "Network " + network + " *stuck!* Last block was at " + lastBlock + " [" + event.getLastBlockNo() + "](" + blockLink + ").",
                true
        );
    }

    private static String toCurrency(CryptoCurrency currency, BigInteger amount) {
        BigInteger hundreds = currency == CryptoCurrency.BTC ? amount.divide(BigInteger.valueOf(100000000L)) : amount.divide(BigInteger.valueOf(10000000000000000L));
        BigInteger[] parts = hundreds.divideAndRemainder(BigInteger.valueOf(100));
        BigInteger eth = parts[0];
        int rem = parts[1].intValue();
        String sRem;
        if (rem == 0) {
            sRem = "";
        }
        else if (rem < 10) {
            sRem = ".0" + rem;
        }
        else {
            sRem = "." + rem;
        }
        return eth + sRem + " " + currency;
    }
}
