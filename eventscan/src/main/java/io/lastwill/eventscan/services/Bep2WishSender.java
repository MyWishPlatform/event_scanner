package io.lastwill.eventscan.services;

import com.binance.dex.api.client.BinanceDexApiNodeClient;
import com.binance.dex.api.client.BinanceDexConstants;
import com.binance.dex.api.client.Wallet;
import com.binance.dex.api.client.domain.Account;
import com.binance.dex.api.client.domain.Balance;
import com.binance.dex.api.client.domain.TransactionMetadata;
import com.binance.dex.api.client.domain.broadcast.TransactionOption;
import com.binance.dex.api.client.domain.broadcast.Transfer;
import io.lastwill.eventscan.events.model.wishbnbswap.*;
import io.lastwill.eventscan.model.CryptoCurrency;
import io.lastwill.eventscan.model.EthToBnbLinkEntry;
import io.lastwill.eventscan.model.EthToBnbSwapEntry;
import io.lastwill.eventscan.repositories.EthToBnbSwapEntryRepository;
import io.mywish.binance.blockchain.services.Wallets;
import io.mywish.scanner.services.EventPublisher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Objects;

@Slf4j
@Component
public class Bep2WishSender {
    private static final BigInteger TRANSFER_FEE = BigInteger.valueOf(37500);

    @Autowired
    private EventPublisher eventPublisher;

    @Autowired
    private BinanceDexApiNodeClient binanceClient;

    @Autowired
    private EthToBnbSwapEntryRepository swapRepository;

    @Autowired
    private ProfileStorage profileStorage;

    @Autowired
    private Wallets wallets;

    private EthBnbProfile ethBnbProfile;

    private Wallet binanceWallet;

    private String burnerAddress;

    private String tokenAddressWish;

    private String transferSymbol;

    private String bnbSymbol;

    @Value("${io.lastwill.eventscan.binance.wish-swap.max-limit:#{null}}")
    private BigInteger coinMaxLimit;

    public void send(EthToBnbSwapEntry swapEntry) {
        if (swapEntry.getLinkEntry() == null) {
            return;
        }

        if (swapEntry.getAmount().equals(BigInteger.ZERO)) {
            return;
        }

        if (coinMaxLimit != null) {
            if (swapEntry.getAmount().compareTo(coinMaxLimit) >= 0) {
                return;
            }

            initSwapToken(swapEntry.getLinkEntry().getSymbol());
        }

        Account account = binanceClient.getAccount(binanceWallet.getAddress());
        BigInteger bnbBalance = getBalance(account, bnbSymbol);
        if (bnbBalance.compareTo(TRANSFER_FEE) < 0) {
            eventPublisher.publish(new LowBalanceEvent(
                    bnbSymbol,
                    CryptoCurrency.BBNB.getDecimals(),
                    swapEntry,
                    TRANSFER_FEE,
                    bnbBalance,
                    binanceWallet.getAddress()
            ));
            return;
        }

        BigInteger coinBalance = getBalance(account, transferSymbol);
        if (coinBalance.equals(BigInteger.ZERO)) {
            return;
        }

        if (coinBalance.compareTo(swapEntry.getAmount()) < 0) {
            eventPublisher.publish(new LowBalanceEvent(
                    transferSymbol,
                    ethBnbProfile.getBnb().getDecimals(),
                    swapEntry,
                    swapEntry.getAmount(),
                    coinBalance,
                    binanceWallet.getAddress()
            ));
            return;
        }

        EthToBnbLinkEntry link = swapEntry.getLinkEntry();
        try {
            List<TransactionMetadata> results = transfer(
                    link.getEthAddress(),
                    link.getBnbAddress(),
                    swapEntry.getAmount()
            );

            TransactionMetadata result = results.get(0);
            if (!result.isOk()) {
                throw new Exception();
            }

            String txHash = result.getHash();
            swapEntry.setBnbTxHash(txHash);
            swapRepository.save(swapEntry);
            log.info("Bep-2 tokens transferred: {}", txHash);

            eventPublisher.publish(new TokensTransferredEvent(
                    transferSymbol,
                    ethBnbProfile.getBnb().getDecimals(),
                    swapEntry
            ));
        } catch (Exception e) {
            log.error("Error when transferring BEP-2 {}.", ethBnbProfile.getEth().getSymbol(), e);

            eventPublisher.publish(new TokensTransferErrorEvent(
                    transferSymbol,
                    ethBnbProfile.getBnb().getDecimals(),
                    swapEntry
            ));
        }
    }

    private void initSwapToken(String ethSymbol) {
        this.ethBnbProfile = profileStorage.getProfileByEthSymbol(ethSymbol);
        this.burnerAddress = ethBnbProfile.getEthBurnerAddress();
        this.tokenAddressWish = ethBnbProfile.getEthTokenAddress();
        this.bnbSymbol = ethBnbProfile.getBnb().getSymbol();
        this.transferSymbol = ethBnbProfile.getTransferSymbol();
        this.binanceWallet = wallets.getWalletBySymbol(ethBnbProfile.getBnb());
    }

    @SuppressWarnings("BigDecimalMethodWithoutRoundingCalled")
    public String toString(BigInteger bnbWishAmount) {
        BigDecimal bdAmount = new BigDecimal(bnbWishAmount)
                .divide(BigDecimal.TEN.pow(ethBnbProfile.getBnb().getDecimals()));

        DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(ethBnbProfile.getBnb().getDecimals());
        df.setMinimumFractionDigits(0);
        df.setGroupingUsed(false);

        return df.format(bdAmount);
    }

    private List<TransactionMetadata> transfer(String ethAddress, String bnbAddress, BigInteger amount)
            throws IOException, NoSuchAlgorithmException {
        Transfer transferObject = buildTransfer(binanceWallet.getAddress(), bnbAddress, transferSymbol, amount);
        TransactionOption transactionOption = buildTransactionOption(buildMemo(ethAddress));
        return binanceClient.transfer(transferObject, binanceWallet, transactionOption, false);
    }

    private String buildMemo(String ethAddress) {
        return "swap from " + ethAddress;
    }

    private TransactionOption buildTransactionOption(String memo) {
        return new TransactionOption(memo, BinanceDexConstants.BINANCE_DEX_API_CLIENT_JAVA_SOURCE, null);
    }

    private Transfer buildTransfer(String from, String to, String coin, BigInteger amount) {
        Transfer transfer = new Transfer();
        transfer.setFromAddress(from);
        transfer.setToAddress(to);
        transfer.setCoin(coin);
        transfer.setAmount(toString(amount));
        return transfer;
    }

    private BigInteger getBalance(Account account, String coin) {
        return account.getBalances()
                .stream()
                .filter(balance -> Objects.equals(balance.getSymbol(), coin))
                .map(Balance::getFree)
                .map(BigInteger::new)
                .findFirst()
                .orElse(BigInteger.ZERO);
    }
}
