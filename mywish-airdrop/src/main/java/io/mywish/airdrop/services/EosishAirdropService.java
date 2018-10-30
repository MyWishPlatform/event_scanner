package io.mywish.airdrop.services;

import io.mywish.airdrop.model.AirdropEntry;
import io.mywish.airdrop.model.BountyAirdropEntry;
import io.mywish.airdrop.model.EosAirdropEntry;
import io.mywish.airdrop.model.WishAirdropEntry;
import io.mywish.airdrop.repositories.AirdropEntryRepository;
import io.mywish.airdrop.repositories.BountyAirdropEntryRepository;
import io.mywish.airdrop.repositories.EosAirdropEntryRepository;
import io.mywish.airdrop.repositories.WishAirdropEntryRepository;
import io.mywish.blockchain.WrapperTransaction;
import io.mywish.scanner.model.NewBlockEvent;
import io.topiacoin.eosrpcadapter.Chain;
import io.topiacoin.eosrpcadapter.Wallet;
import io.topiacoin.eosrpcadapter.exceptions.ChainException;
import io.topiacoin.eosrpcadapter.exceptions.WalletException;
import io.topiacoin.eosrpcadapter.messages.*;
import javafx.scene.effect.Light;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Component
public class EosishAirdropService {
    @Autowired
    private WishAirdropEntryRepository wishRepository;
    @Autowired
    private BountyAirdropEntryRepository bountyRepository;
    @Autowired
    private EosAirdropEntryRepository eosRepository;

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    private EosAdapter eosAdapter;
    @Value("${io.mywish.airdrop.private-key}")
    private String privateKey;
    @Value("${io.mywish.airdrop.public-key}")
    private String publicKey;
    @Value("${io.mywish.airdrop.symbol-format}")
    private String symbolFormat;
    @Value("${io.mywish.airdrop.sender}")
    private String sender;
    @Value("${io.mywish.airdrop.memo}")
    private String memo;
    @Value("${io.mywish.airdrop.token-account}")
    private String tokenAccount;

    private Chain chainApi;
    private Wallet walletApi;
    private String walletPassword;
    private ChainInfo chainInfo;
    private BlockInfo blockInfo;

    @PostConstruct
    protected void init() throws WalletException, ChainException {
        chainApi = eosAdapter.chain();
        walletApi = eosAdapter.wallet();

        walletPassword = walletApi.create(null);
        walletApi.importKey(null, privateKey);

        chainInfo = chainApi.getInfo();
        blockInfo = chainApi.getBlock(chainInfo.last_irreversible_block_id);
    }

    @EventListener
    protected void onNewBlock(NewBlockEvent event) {
        if (!event.getTransactionsByAddress().containsKey(tokenAccount)) {
            return;
        }
        event.getTransactionsByAddress()
                .get(tokenAccount)
                .stream()
                .map(WrapperTransaction::getHash)
                .peek(log::info)
                .map(this::findByTxHash)
                .flatMap(Function.identity())
                .peek(airdropEntry -> log.info("{}", airdropEntry))
                .forEach(airdropEntry -> {
                    getRepository(airdropEntry.getClass()).txInBlock(airdropEntry, event.getBlock().getNumber());
                    log.info("Entry {} in block {}.", airdropEntry.getId(), event.getBlock().getNumber());
                });
    }

    @Transactional
    public List<AirdropEntry> findFistWish(int limit) {
        try (Stream<WishAirdropEntry> stream = getRepository(WishAirdropEntry.class).findFirstNotProcessed()) {
            return stream
                    .limit(limit)
                    .collect(Collectors.toList());
        }
    }

    @Transactional
    public List<AirdropEntry> findFistBounty(int limit) {
        try (Stream<BountyAirdropEntry> stream = getRepository(BountyAirdropEntry.class).findFirstNotProcessed()) {
            return stream
                    .limit(limit)
                    .collect(Collectors.toList());
        }
    }

    @Transactional
    public List<AirdropEntry> findFistEos(int limit) {
        try (Stream<EosAirdropEntry> stream = getRepository(EosAirdropEntry.class).findFirstNotProcessed()) {
            return stream
                    .limit(limit)
                    .collect(Collectors.toList());
        }
    }

    public void update(List<AirdropEntry> entries) throws ChainException, WalletException {
        for (int i = 0; i < entries.size(); i++) {
            AirdropEntry entry = entries.get(i);
            if (getRepository(entry.getClass()).process(entry) == 0) {
                log.info("Skip entry {}, already is in process.", entry.getId());
                continue;
            }
            BigDecimal eosish = getEosishAmount(entry);

            DecimalFormat decimalFormat = new DecimalFormat(symbolFormat);
            log.info("Ready to send {} eosish ({}). {}/{} done.", eosish, decimalFormat.format(eosish), i, entries.size());
            Transaction.Action action = null;
            try {
                action = buildAction(entry.getEosAddress(), decimalFormat.format(eosish));
            }
            catch (Exception e) {
                log.error("Error on creating action. {}/{} done.", e, i, entries.size());
                throw e;
            }
            String hash = sendAction(action);
            if (hash == null) {
                log.error("Sending failed.");
                continue;
            }

            getRepository(entry.getClass()).txSent(entry, hash, eosish, LocalDateTime.now(ZoneOffset.UTC));
        }
    }

    protected Stream<? extends AirdropEntry> findByTxHash(final String txHash) {
        return Stream.of(wishRepository, bountyRepository, eosRepository)
                .map(repository -> repository.findByTxHash(txHash))
                .flatMap(List::stream);
    }

    protected <T extends AirdropEntry, R extends AirdropEntryRepository<T>> R getRepository(Class<T> entryClass) {
        if (entryClass == BountyAirdropEntry.class) {
            return (R) bountyRepository;
        }
        else if (entryClass == WishAirdropEntry.class) {
            return (R) wishRepository;
        }
        else if (entryClass == EosAirdropEntry.class) {
            return (R) eosRepository;
        }
        throw new UnsupportedOperationException("Airdrop " + entryClass.getSimpleName() + " is not supported.");
    }

    protected BigDecimal getEosishAmount(AirdropEntry airdropEntry) {
        if (airdropEntry instanceof WishAirdropEntry) {
            BigDecimal wish = new BigDecimal(((WishAirdropEntry) airdropEntry).getWishAmount());
            return wish.divide(BigDecimal.TEN.pow(18), 4, RoundingMode.HALF_UP);
        }
        else if (airdropEntry instanceof BountyAirdropEntry) {
            return ((BountyAirdropEntry) airdropEntry).getBonusAmount();
        }
        else if (airdropEntry instanceof EosAirdropEntry) {
            return ((EosAirdropEntry) airdropEntry).getEosAmount().divide(BigDecimal.valueOf(50), 4, RoundingMode.HALF_UP);
        }
        throw new UnsupportedOperationException("Airdrop " + airdropEntry.getClass().getSimpleName() + " is not supported.");
    }

    private Transaction.Action buildAction(String to, String quantity) throws ChainException {
        Map params = new HashMap<String, String>() {{
            put("from", sender);
            put("to", to);
            put("quantity", quantity);
            put("memo", memo);
        }};
        TransactionBinArgs transactionBinArgs = chainApi.abiJsonToBin(tokenAccount, "transfer", params);

        return new Transaction.Action(
                tokenAccount,
                "transfer",
                Collections.singletonList(new Transaction.Authorization(sender, "active")),
                transactionBinArgs.binargs
        );
    }

    private String sendAction(Transaction.Action action) throws WalletException, ChainException {
        SignedTransaction signedTransaction = null;
        for (int i = 0; i < 10; i ++) {
            try {
                String expiration = LocalDateTime.now(ZoneOffset.UTC).plus(10, ChronoUnit.MINUTES).format(
                        DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
                );

                Transaction transaction = new Transaction(
                        expiration,
                        chainInfo.last_irreversible_block_num,
                        blockInfo.ref_block_prefix,
                        0,
                        0,
                        0,
                        null,
                        Collections.singletonList(action),
                        null,
                        null,
                        null
                );

                signedTransaction = walletApi.signTransaction(
                        transaction,
                        Collections.singletonList(publicKey),
                        chainInfo.chain_id
                );
            }
            catch (WalletException e) {
                log.warn("Sing failed. Try again {} time.", i, e);
                try {
                    Thread.sleep(1000);
                }
                catch (InterruptedException e2) {
                    log.error("Sleep was interrupted.", e2);
                }
            }
        }
        if (signedTransaction == null) {
            throw new WalletException("Singe transaction failed multiple times.");
        }
        ChainException last = null;
        for (int i = 0; i < 30; i++) {
            try {
                log.info("Sending try {}.", i);
                Transaction.Response response = chainApi.pushTransaction(signedTransaction);
                log.info("Response: {}", response);
                return response.transaction_id;
            }
            catch (ChainException ex) {
                last = ex;
                log.error("Send failed.", ex);
                if (ex.getMessage().contains("tx_cpu_usage_exceeded")) {
                    try {
                        log.warn("tx_cpu_usage_exceeded, wait 1 sec.");
                        Thread.sleep(1000);
                    }
                    catch (InterruptedException e) {
                        log.error("Sleep was interrupted.", e);
                    }
                    continue;
                }
                else if (ex.getMessage().contains("assertion failure with message")) {
                    log.warn("Skip this error.");
                    return null;
                }
                throw ex;
            }
        }
        throw last;
    }
}
