package io.mywish.airdrop.services;

import io.mywish.airdrop.model.EosishAirdropEntry;
import io.mywish.airdrop.repositories.EosishAirdropEntryRepository;
import io.mywish.blockchain.WrapperTransaction;
import io.mywish.scanner.model.NewBlockEvent;
import io.topiacoin.eosrpcadapter.Chain;
import io.topiacoin.eosrpcadapter.Wallet;
import io.topiacoin.eosrpcadapter.exceptions.ChainException;
import io.topiacoin.eosrpcadapter.exceptions.WalletException;
import io.topiacoin.eosrpcadapter.messages.*;
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
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Component
public class EosishAirdropService {
    @Autowired
    private EosishAirdropEntryRepository repository;

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
                .map(repository::findByTxHash)
                .map(List::stream)
                .flatMap(Function.identity())
                .peek(airdropEntry -> log.info("{}", airdropEntry))
                .forEach(airdropEntry -> {
                    repository.txInBlock(airdropEntry, event.getBlock().getNumber());
                    log.info("Entry {} in block {}.", airdropEntry.getId(), event.getBlock().getNumber());
                });
    }

    @Transactional
    public List<EosishAirdropEntry> findFist(int limit) {
        try (Stream<EosishAirdropEntry> stream = repository.findFirstNotProcessed()) {
            return stream
                    .limit(limit)
                    .collect(Collectors.toList());
        }
    }

    public void update(List<EosishAirdropEntry> entries) throws ChainException, WalletException {
        for (EosishAirdropEntry entry : entries) {
            if (repository.process(entry) == 0) {
                log.info("Skip entry {}, already is in process.", entry.getId());
                continue;
            }
            BigDecimal wish = new BigDecimal(entry.getWishAmount());
            DecimalFormat decimalFormat = new DecimalFormat(symbolFormat);
            BigDecimal eosish = wish.divide(BigDecimal.TEN.pow(18), 4, RoundingMode.HALF_UP);

            Transaction.Action action = null;
            try {
                action = buildAction(entry.getEosAddress(), decimalFormat.format(eosish));
//                break;
            }
            catch (Exception e) {
//                if (e.getMessage().contains("invalid_action_args_exception")) {
//                    continue;
//                }
                log.error("Error on creating action.", e);
                throw e;
            }
            String hash = sendAction(action);

            repository.txSent(entry, hash, eosish, LocalDateTime.now(ZoneOffset.UTC));
        }
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
        String expiration = LocalDateTime.now(ZoneOffset.UTC).plus(1, ChronoUnit.HOURS).format(
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

        SignedTransaction signedTransaction = walletApi.signTransaction(
                transaction,
                Collections.singletonList(publicKey),
                chainInfo.chain_id
        );
        ChainException last = null;
        for (int i = 0; i < 30; i++) {
            try {
                Transaction.Response response = chainApi.pushTransaction(signedTransaction);
                log.info("Response: {}", response);
                return response.transaction_id;
            }
            catch (ChainException ex) {
                last = ex;
                log.error("Send failed.", ex);
                if (ex.getMessage().contains("tx_cpu_usage_exceeded")) {
                    continue;
                }
                throw ex;
            }
        }
        throw last;
    }
}
