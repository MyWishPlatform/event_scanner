package io.mywish.bot.integration.services;

import io.lastwill.eventscan.events.model.ContractCreatedEvent;
import io.lastwill.eventscan.events.model.Swaps2NotificationMQEvent;
import io.lastwill.eventscan.events.model.Swaps2OrderCreatedEvent;
import io.lastwill.eventscan.model.*;
import io.lastwill.eventscan.repositories.UserRepository;
import io.mywish.bot.service.MyWishBotLight;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class BotIntegrationLight {
    @Autowired
    private MyWishBotLight bot;


    @Autowired
    private UserRepository userRepository;

    @Value("${io.lastwill.eventscan.contract.crowdsale-address}")
    private String crowdsaleAddress;

    @Autowired
    protected BlockchainExplorerProvider explorerProvider;

    private final ZoneId localZone = ZoneId.of("Europe/Moscow");
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final Map<NetworkType, String> networkName = new HashMap<NetworkType, String>() {{
        put(NetworkType.ETHEREUM_MAINNET, "ETH");
        put(NetworkType.ETHEREUM_ROPSTEN, "tETH");
        put(NetworkType.RSK_MAINNET, "RSK");
        put(NetworkType.RSK_TESTNET, "tRSK");
        put(NetworkType.BTC_MAINNET, "BTC");
        put(NetworkType.BTC_TESTNET_3, "tBTC");
        put(NetworkType.NEO_MAINNET, "NEO");
        put(NetworkType.NEO_TESTNET, "tNEO");
        put(NetworkType.EOS_TESTNET, "tEOS");
        put(NetworkType.EOS_MAINNET, "EOS");
        put(NetworkType.TRON_TESTNET, "tTRON");
        put(NetworkType.TRON_MAINNET, "TRON");
        put(NetworkType.WAVES_TESTNET, "tWAVES");
        put(NetworkType.WAVES_MAINNET, "WAVES");
        put(NetworkType.BINANCE_TESTNET, "tBNB");
        put(NetworkType.BINANCE_MAINNET, "BNB");
    }};

    private final String defaultNetwork = "unknown";

    private String getUser(int userId) {
        User user = userRepository.findOne(userId);
        String email = user.getEmail();
        return email != null && !email.isEmpty() ? email : "user " + userId;
    }

    @SuppressWarnings("BigDecimalMethodWithoutRoundingCalled")
    private static String toCurrency(CryptoCurrency currency, BigInteger amount) {
        BigDecimal bdAmount = new BigDecimal(amount)
                .divide(BigDecimal.TEN.pow(currency.getDecimals()));

        DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(currency.getDecimals());
        df.setMinimumFractionDigits(0);

        return df.format(bdAmount) + " " + currency;
    }


    @EventListener
    private void onContractCreated(final ContractCreatedEvent contractCreatedEvent) {
        final Contract contract = contractCreatedEvent.getContract();
        final Product product = contract.getProduct();
        final String type = ProductStatistics.PRODUCT_TYPES.get(product.getContractType());
        final String user = getUser(product.getUserId());
        final String network = networkName.getOrDefault(product.getNetwork().getType(), defaultNetwork);
        final String txLink = explorerProvider.getOrStub(product.getNetwork().getType())
                .buildToTransaction(contractCreatedEvent.getTransaction().getHash());
        final String addressLink = explorerProvider.getOrStub(product.getNetwork().getType())
                .buildToAddress(contractCreatedEvent.getAddress());

        if (contractCreatedEvent.isSuccess()) {
            bot.onContract(
                    network,
                    product.getId(),
                    type,
                    contract.getId(),
                    toCurrency(CryptoCurrency.USD, product.getCost()),
                    user,
                    contractCreatedEvent.getAddress(),
                    addressLink
            );
        } else {
            bot.onContractFailed(network, product.getId(), type, contract.getId(), txLink);
        }
    }

    @EventListener
    private void onSwaps2NotificationMQ(final Swaps2NotificationMQEvent event) {
        Swaps2Order order = event.getOrder();
        Integer userId = order.getUser();

        bot.onSwapsOrderFromDataBase(
                order.getId(),
                order.getName(),
                getUser(userId)
        );
    }

    @EventListener
    private void onSwaps2OrderCreated(final Swaps2OrderCreatedEvent event) {
        Swaps2Order order = event.getOrder();
        String network = networkName.getOrDefault(event.getNetworkType(), defaultNetwork);
        String txHash = event.getTransaction().getHash();
        String txLink = explorerProvider.getOrStub(event.getNetworkType())
                .buildToTransaction(txHash);

        bot.onSwapsOrder(network, order.getId(), order.getName(), txHash, txLink);
    }

}
