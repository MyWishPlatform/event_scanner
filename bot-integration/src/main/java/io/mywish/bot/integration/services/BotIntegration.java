package io.mywish.bot.integration.services;

import io.lastwill.eventscan.events.model.*;
import io.lastwill.eventscan.events.model.contract.CreateAccountEvent;
import io.lastwill.eventscan.events.model.contract.eos.CreateTokenEvent;
import io.lastwill.eventscan.events.model.utility.NetworkStuckEvent;
import io.lastwill.eventscan.events.model.utility.PendingStuckEvent;
import io.lastwill.eventscan.events.model.wishbnbswap.LowBalanceEvent;
import io.lastwill.eventscan.events.model.wishbnbswap.TokensBurnedEvent;
import io.lastwill.eventscan.events.model.wishbnbswap.TokensTransferErrorEvent;
import io.lastwill.eventscan.events.model.wishbnbswap.TokensTransferredEvent;
import io.lastwill.eventscan.model.*;
import io.mywish.blockchain.ContractEvent;
import io.mywish.bot.service.MyWishBot;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
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

    @EventListener
    private void onContractCreated(final ContractCreatedEvent contractCreatedEvent) {
        final Contract contract = contractCreatedEvent.getContract();
        final Product product = contract.getProduct();
        final String type = ProductStatistics.PRODUCT_TYPES.get(product.getContractType());
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
                    contractCreatedEvent.getAddress(),
                    addressLink
            );
        } else {
            bot.onContractFailed(network, product.getId(), type, contract.getId(), txLink);
        }
    }

    @EventListener
    private void onWishSwapLowBalance(final LowBalanceEvent event) {
        Long linkId = event.getSwapEntry().getLinkEntry().getId();
        Long swapId = event.getSwapEntry().getId();
        String fromAddress = event.getFromAddress();
        String fromAddressLink = explorerProvider.getOrStub(NetworkType.BINANCE_MAINNET)
                .buildToAddress(fromAddress);
        String toAddress = event.getSwapEntry().getLinkEntry().getBnbAddress();
        String toAddressLink = explorerProvider.getOrStub(NetworkType.BINANCE_MAINNET)
                .buildToAddress(toAddress);
        String ethAddress = event.getSwapEntry().getLinkEntry().getEthAddress();
        String ethAddressLink = explorerProvider.getOrStub(NetworkType.ETHEREUM_MAINNET)
                .buildToAddress(ethAddress);
        String need = toCurrency(event.getCoin(), event.getDecimals(), event.getNeed());
        String have = toCurrency(event.getCoin(), event.getDecimals(), event.getHave());

        bot.onWishSwapLowBalance(linkId, swapId, fromAddress, fromAddressLink, toAddress, toAddressLink, ethAddress, ethAddressLink, need, have);
    }

    @EventListener
    private void onWishSwapBurn(final TokensBurnedEvent event) {
        Long linkId = event.getSwapEntry().getLinkEntry().getId();
        Long swapId = event.getSwapEntry().getId();
        String ethAddress = event.getEthAddress();
        String ethAddressLink = explorerProvider.getOrStub(NetworkType.ETHEREUM_MAINNET)
                .buildToAddress(ethAddress);
        String bnbAddress = event.getBnbAddress() != null ? event.getBnbAddress() : "not linked";
        String bnbAddressLink = event.getBnbAddress() != null
                ? explorerProvider.getOrStub(NetworkType.BINANCE_MAINNET).buildToAddress(bnbAddress)
                : "";
        String amount = toCurrency(event.getCoin(), event.getDecimals(), event.getSwapEntry().getAmount());
        String burnTx = explorerProvider.getOrStub(NetworkType.ETHEREUM_MAINNET)
                .buildToTransaction(event.getSwapEntry().getEthTxHash());
        bot.onWishSwapBurn(linkId, swapId, ethAddress, ethAddressLink, bnbAddress, bnbAddressLink, amount, burnTx);
    }

    @EventListener
    private void onWishSwapTransferError(final TokensTransferErrorEvent event) {
        Long linkId = event.getWishEntry().getLinkEntry().getId();
        Long swapId = event.getWishEntry().getId();
        String amount = toCurrency(event.getCoin(), event.getDecimals(), event.getWishEntry().getAmount());
        String bnbTxHash = event.getWishEntry().getBnbTxHash();
        String bnbTxHashLink = bnbTxHash != null
                ? explorerProvider.getOrStub(NetworkType.BINANCE_MAINNET).buildToTransaction(bnbTxHash)
                : "";
        String bnbAddress = event.getWishEntry().getLinkEntry().getBnbAddress();
        String bnbAddressLink = bnbAddress != null
                ? explorerProvider.getOrStub(NetworkType.BINANCE_MAINNET).buildToAddress(bnbAddress)
                : "";
        String ethAddress = event.getWishEntry().getLinkEntry().getEthAddress();
        String ethAddressLink = ethAddress != null
                ? explorerProvider.getOrStub(NetworkType.ETHEREUM_MAINNET).buildToAddress(ethAddress)
                : "";
        bot.onWishSwapTransferError(linkId, swapId, amount, bnbTxHashLink, bnbAddress, bnbAddressLink, ethAddress, ethAddressLink);
    }

    @EventListener
    private void onWishSwapTransfer(final TokensTransferredEvent event) {
        Long linkId = event.getWishEntry().getLinkEntry().getId();
        Long swapId = event.getWishEntry().getId();
        String amount = toCurrency(event.getCoin(), event.getDecimals(), event.getWishEntry().getAmount());
        String transferTxLink = explorerProvider.getOrStub(NetworkType.BINANCE_MAINNET)
                .buildToTransaction(event.getWishEntry().getBnbTxHash());
        String bnbAddress = event.getWishEntry().getLinkEntry().getBnbAddress();
        String bnbAddressLink = explorerProvider.getOrStub(NetworkType.BINANCE_MAINNET)
                .buildToAddress(bnbAddress);
        String ethAddress = event.getWishEntry().getLinkEntry().getEthAddress();
        String ethAddressLink = explorerProvider.getOrStub(NetworkType.ETHEREUM_MAINNET)
                .buildToAddress(ethAddress);

        bot.onWishSwapTransfer(linkId, swapId, amount, transferTxLink, bnbAddress, bnbAddressLink, ethAddress, ethAddressLink);
    }

    @EventListener
    private void onSwapsOrderCreated(final SwapsOrderCreatedEvent event) {
        ProductSwaps2 product = event.getProduct();
        String network = networkName.getOrDefault(product.getNetwork().getType(), defaultNetwork);
        String txHash = event.getTransaction().getHash();
        String txLink = explorerProvider.getOrStub(product.getNetwork().getType())
                .buildToTransaction(txHash);

        bot.onSwapsOrder(network, product.getId(), txHash, txLink);
    }

    @EventListener
    private void onOwnerBalanceChanged(final UserPaymentEvent event) {
        try {
            final UserSiteBalance userSiteBalance = event.getUserSiteBalance();
            final String network = networkName.getOrDefault(event.getNetworkType(), defaultNetwork);
            final String txLink = explorerProvider.getOrStub(event.getNetworkType())
                    .buildToTransaction(event.getTransaction().getHash());
            final String email = userSiteBalance.getUser().getEmail();
            final String id = Integer.toString(userSiteBalance.getUser().getId());
            bot.onBalance(
                    network,
                    email != null ? email : id,
                    toCurrency(event.getCurrency(), event.getAmount()),
                    txLink
            );
        } catch (Exception e) {
            log.error("Error on sending payment info to the bot.", e);
        }
    }

    @EventListener
    private void onRskFGWBalanceChanged(final FGWBalanceChangedEvent event) {
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
    private void onBtcPaymentChanged(final ProductPaymentEvent event) {
        final String network = networkName.getOrDefault(event.getNetworkType(), defaultNetwork);
        final Product product = event.getProduct();
        final String type = ProductStatistics.PRODUCT_TYPES.get(product.getContractType());
        final String txLink = explorerProvider.getOrStub(event.getNetworkType())
                .buildToTransaction(event.getTransactionOutput().getParentTransaction());
        bot.onBtcPayment(
                network,
                type,
                product.getId(),
                toCurrency(event.getCurrency(), event.getAmount()),
                txLink
        );
    }

    @EventListener
    private void onNeoPayment(final NeoPaymentEvent event) {
        final String network = networkName.getOrDefault(event.getNetworkType(), defaultNetwork);
        final String address = event.getAddress();
        final String value = toCurrency(event.getCurrency(), event.getAmount());
        final String txLink = explorerProvider.getOrStub(event.getNetworkType())
                .buildToTransaction(event.getNeoTransaction().getHash());
        bot.onNeoPayment(
                network,
                address,
                value,
                txLink
        );
    }

    @EventListener
    private void onNetworkStuck(final NetworkStuckEvent event) {
        final String network = networkName.getOrDefault(event.getNetworkType(), defaultNetwork);
        String lastBlock = formatToLocal(event.getReceivedTime());
        final String blockLink = explorerProvider
                .getOrStub(event.getNetworkType())
                .buildToBlock(event.getLastBlockNo());
        bot.sendToAll(
                "Network " + network + " *stuck!* Last block was at " + lastBlock + " [" + event.getLastBlockNo() + "](" + blockLink + ").",
                true
        );
    }

    @EventListener
    private void onPendingStuck(final PendingStuckEvent event) {
        final String network = networkName.getOrDefault(event.getNetworkType(), defaultNetwork);
        String lastBlock = formatToLocal(event.getReceivedTime());
        bot.sendToAll(
                "*No pending transactions* for the network " + network + "! Last pending was at " + lastBlock + ", count: " + event.getCount() + ".",
                true
        );
    }

    @EventListener
    private void onContractEvent(final ContractEventsEvent event) {
        final String network = networkName.getOrDefault(event.getNetworkType(), defaultNetwork);

        for (ContractEvent contractEvent : event.getEvents()) {
            if (contractEvent instanceof CreateAccountEvent) {
                final String accountRef = explorerProvider.getOrStub(event.getNetworkType())
                        .buildToAddress(((CreateAccountEvent) contractEvent).getCreated());

                bot.sendToAll(
                        network + ": account [" + ((CreateAccountEvent) contractEvent).getCreated() + "](" + accountRef + ") created.",
                        true
                );
            } else if (contractEvent instanceof CreateTokenEvent) {
                CreateTokenEvent createTokenEvent = (CreateTokenEvent) contractEvent;
                final String tokenRef = explorerProvider.getOrStub(event.getNetworkType())
                        .buildToAddress(createTokenEvent.getAddress());
                final String accountRef = explorerProvider.getOrStub(event.getNetworkType())
                        .buildToAddress(createTokenEvent.getIssuer());
                bot.sendToAll(
                        network + ": token [" + createTokenEvent.getSymbol() + "](" + tokenRef + ") create by [" + createTokenEvent.getIssuer() + "](" + accountRef + ").",
                        true
                );
            }
        }
    }

    private static String toCurrency(CryptoCurrency currency, BigInteger amount) {
        return toCurrency(currency.toString(), currency.getDecimals(), amount);
    }

    @SuppressWarnings("BigDecimalMethodWithoutRoundingCalled")
    private static String toCurrency(String currency, int decimals, BigInteger amount) {
        BigDecimal bdAmount = new BigDecimal(amount)
                .divide(BigDecimal.TEN.pow(decimals));

        DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(decimals);
        df.setMinimumFractionDigits(0);

        return df.format(bdAmount) + " " + currency;
    }

    private String formatToLocal(LocalDateTime localDateTime) {
        return ZonedDateTime.ofInstant(
                localDateTime.toInstant(ZoneOffset.UTC),
                this.localZone
        )
                .format(dateFormatter);
    }
}
