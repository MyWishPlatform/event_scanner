package io.lastwill.eventscan.services;

import com.binance.dex.api.client.BinanceDexApiNodeClient;
import com.binance.dex.api.client.BinanceDexConstants;
import com.binance.dex.api.client.Wallet;
import com.binance.dex.api.client.domain.Account;
import com.binance.dex.api.client.domain.Balance;
import com.binance.dex.api.client.domain.TransactionMetadata;
import com.binance.dex.api.client.domain.broadcast.TransactionOption;
import com.binance.dex.api.client.domain.broadcast.Transfer;
import io.lastwill.eventscan.events.model.wishbnbswap.LowBalanceEvent;
import io.lastwill.eventscan.events.model.wishbnbswap.TokensTransferErrorEvent;
import io.lastwill.eventscan.events.model.wishbnbswap.TokensTransferredEvent;
import io.lastwill.eventscan.model.CryptoCurrency;
import io.lastwill.eventscan.model.WishToBnbLinkEntry;
import io.lastwill.eventscan.model.WishToBnbSwapEntry;
import io.lastwill.eventscan.repositories.WishToBnbSwapEntryRepository;
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
    private static final BigInteger TRANSFER_FEE = BigInteger.valueOf(62500);

    @Autowired
    private EventPublisher eventPublisher;

    @Autowired
    private BinanceDexApiNodeClient binanceClient;

    @Autowired
    private WishToBnbSwapEntryRepository swapRepository;

    @Autowired
    private Wallet binanceWallet;

    @Value("${io.lastwill.eventscan.binance.wish-swap.burner-address}")
    private String burnerAddress;

    @Value("${io.lastwill.eventscan.contract.token-address.wish}")
    private String tokenAddressWish;

    @Value("${io.lastwill.eventscan.binance-wish.token-symbol}")
    private String bnbWishSymbol;

    @Value("${io.lastwill.eventscan.binance.token-symbol}")
    private String bnbSymbol;

    @Value("${io.lastwill.eventscan.binance.wish-swap.max-limit:#{null}}")
    private BigInteger wishMaxLimit;

    public void send(WishToBnbSwapEntry swapEntry) {
        if (swapEntry.getLinkEntry() == null) {
            return;
        }

        if (swapEntry.getAmount().equals(BigInteger.ZERO)) {
            return;
        }

        if (wishMaxLimit != null) {
            if (swapEntry.getAmount().compareTo(wishMaxLimit) >= 0) {
                return;
            }
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

        BigInteger wishBalance = getBalance(account, bnbWishSymbol);
        if (wishBalance.equals(BigInteger.ZERO)) {
            return;
        }

        if (wishBalance.compareTo(swapEntry.getAmount()) < 0) {
            eventPublisher.publish(new LowBalanceEvent(
                    bnbWishSymbol,
                    CryptoCurrency.BWISH.getDecimals(),
                    swapEntry,
                    swapEntry.getAmount(),
                    wishBalance,
                    binanceWallet.getAddress()
            ));
            return;
        }

        WishToBnbLinkEntry link = swapEntry.getLinkEntry();
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
                    bnbWishSymbol,
                    CryptoCurrency.BWISH.getDecimals(),
                    swapEntry
            ));
        } catch (Exception e) {
            log.error("Error when transferring BEP-2 WISH.", e);

            eventPublisher.publish(new TokensTransferErrorEvent(
                    bnbWishSymbol,
                    CryptoCurrency.BWISH.getDecimals(),
                    swapEntry
            ));
        }
    }

    @SuppressWarnings("BigDecimalMethodWithoutRoundingCalled")
    public String toString(BigInteger bnbWishAmount) {
        BigDecimal bdAmount = new BigDecimal(bnbWishAmount)
                .divide(BigDecimal.TEN.pow(CryptoCurrency.BWISH.getDecimals()));

        DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(CryptoCurrency.BWISH.getDecimals());
        df.setMinimumFractionDigits(0);

        return df.format(bdAmount);
    }

    private List<TransactionMetadata> transfer(String ethAddress, String bnbAddress, BigInteger amount)
            throws IOException, NoSuchAlgorithmException {
        Transfer transferObject = buildTransfer(binanceWallet.getAddress(), bnbAddress, bnbWishSymbol, amount);
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
