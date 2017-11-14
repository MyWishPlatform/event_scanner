package io.mywish.dream.service;

import io.mywish.dream.exception.ContractInvocationException;
import io.mywish.dream.exception.CreationException;
import io.mywish.dream.exception.UnlockAddressException;
import io.mywish.dream.model.Contract;
import io.mywish.dream.model.Player;
import io.mywish.dream.model.contracts.TicketHolder;
import io.mywish.dream.model.contracts.TicketSale;
import io.mywish.dream.repositories.ContractRepository;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.admin.Admin;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tx.ClientTransactionManager;
import org.web3j.tx.TransactionManager;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

@Slf4j
@Component
public class TicketService {
    private static final String EMPTY_ADDRESS = "0x0000000000000000000000000000000000000000";
    private static final BigInteger TICKET_PRICE = new BigInteger("100000000000000000");
    @Autowired
    private Web3j web3j;
    @Autowired
    private Admin admin;
    @Autowired
    private ContractRepository contractRepository;
    @Value("${io.mywish.dream.server-address}")
    private String serverAddress;
    @Value("${io.mywish.dream.server-account-password}")
    private String serverAccountPassword;
    @Value("${io.mywish.dream.gas-price}")
    private BigInteger gasPrice;
    @Value("${io.mywish.dream.gas-limit}")
    private BigInteger gasLimit;

    private TransactionManager transactionManager;

    private static CompletionStage<Player> loadPlayer(final int index, final TicketHolder ticketHolder) {
        return ticketHolder.getTickets(BigInteger.valueOf(index)).sendAsync()
                .thenApply(tuple -> new Player(index, tuple.getValue1(), tuple.getValue2(), tuple.getValue3()));
    }

    private static CompletionStage<List<Player>> recurseCollector(final Player player, final List<Player> list, final TicketHolder ticketHolder) {
        if (EMPTY_ADDRESS.equals(player.getAddress())) {
            return CompletableFuture.completedFuture(list);
        }
        list.add(player);
        return loadPlayer(player.getIndex() + 1, ticketHolder)
                .thenCompose(player1 -> recurseCollector(player1, list, ticketHolder));
    }

    @PostConstruct
    protected void init() throws IOException {
        transactionManager = new ClientTransactionManager(web3j, serverAddress);
        admin.personalUnlockAccount(serverAddress, serverAccountPassword) // BigInteger.valueOf(2_592_000)
                .send();
    }

    public CompletionStage<Contract> deploy(LocalDateTime endDate) {
        return unlockInvoke()
                .thenCompose(v -> TicketSale.deploy(web3j, transactionManager, gasPrice, gasLimit, BigInteger.valueOf(endDate.toEpochSecond(ZoneOffset.UTC))).sendAsync())
                .thenApply(ticketSale -> {
                    TransactionReceipt receipt = ticketSale.getTransactionReceipt()
                            .orElseThrow(() -> new CreationException("Contract creation finished, but there is no receipt."));
                    if ("0".equals(receipt.getStatus())) {
                        throw new CreationException("Transaction " + receipt.getTransactionHash() + " finished with failed status.");
                    }

                    Contract contract = new Contract(ticketSale.getContractAddress(), serverAddress, receipt.getTransactionHash());
                    contractRepository.save(contract);

                    return contract;
                });
    }

    public Iterable<Contract> getContracts() {
        return contractRepository.findAll();
    }

    public CompletionStage<List<Player>> getPlayers(final String contractAddress) {
        TicketSale ticketSale = TicketSale.load(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
        return ticketSale.ticketHolder().sendAsync()
                .thenCompose(ticketHolderAddress -> {
                    final TicketHolder ticketHolder = TicketHolder.load(ticketHolderAddress, web3j, transactionManager, gasPrice, gasLimit);
                    return loadPlayer(0, ticketHolder)
                            .thenCompose(player -> recurseCollector(player, new ArrayList<>(), ticketHolder));
                });
    }

    public CompletionStage<Void> finish(final String contractAddress) {
        final TicketSale ticketSale = TicketSale.load(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
        return unlockInvoke()
                .thenCompose(v -> ticketSale.finish().sendAsync())
                .thenAccept(receipt -> {
                    if ("0".equals(receipt.getStatus())) {
                        throw new ContractInvocationException("Transaction (" + receipt.getTransactionHash() + ") failed: contract TicketSale(" + contractAddress + ").finish().");
                    }
                });
    }

    public CompletionStage<Boolean> isFinished(final String contractAddress) {
        final TicketSale ticketSale = TicketSale.load(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
        return ticketSale.isEnded().sendAsync();
    }

    public CompletionStage<Void> setWinner(final String contractAddress, final int index) {
        TicketSale ticketSale = TicketSale.load(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
        return unlockInvoke()
                .thenCompose(v -> ticketSale.setWinner(BigInteger.valueOf(index)).sendAsync())
                .thenAccept(receipt -> {
                    if ("0".equals(receipt.getStatus())) {
                        throw new ContractInvocationException("Transaction (" + receipt.getTransactionHash() + ") failed: contract TicketSale(" + contractAddress + ").setWinner(" + index + ").");
                    }
                });
    }

    public CompletionStage<Void> payToLast(final String contractAddress) {
        final TicketSale ticketSale = TicketSale.load(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
        return ticketSale.ticketHolder().sendAsync()
                .thenCompose(ticketHolderAddress -> {
                    final TicketHolder ticketHolder = TicketHolder.load(ticketHolderAddress, web3j, transactionManager, gasPrice, gasLimit);
                    return ticketHolder.totalTickets().sendAsync()
                            .thenApply(totalTickets -> new ComposedTickets(ticketHolder, totalTickets));
                })
                .thenCompose(composedInfo -> composedInfo.ticketHolder.getPlayersCount().sendAsync()
                        .thenApply(playerCount -> new ComposedTickets(composedInfo.ticketHolder, composedInfo.totalTickets, playerCount)))
                .thenCompose(composedInfo -> {
                    final BigInteger playerIndex = composedInfo.playerCount.subtract(BigInteger.ONE);
                    final BigInteger weiAmount = composedInfo.totalTickets.multiply(TICKET_PRICE);
                    return unlockInvoke()
                            .thenCompose(v -> ticketSale.payPrize(playerIndex, weiAmount).sendAsync())
                            .thenAccept(receipt -> {
                                if ("0".equals(receipt.getStatus())) {
                                    throw new ContractInvocationException("Transaction (" + receipt.getTransactionHash() + ") failed: contract TicketSale(" + contractAddress + ").payPrize(" + playerIndex + ", " + weiAmount + ")");
                                }
                            });
                });
    }

    private CompletionStage<Void> unlockInvoke() {
        return admin.personalUnlockAccount(serverAddress, serverAccountPassword)
                .sendAsync()
                .thenAccept(personalUnlockAccount -> {
                    if (personalUnlockAccount.getResult() == null || !personalUnlockAccount.getResult()) {
                        throw new UnlockAddressException("Impossible to unlock account " + serverAddress + ".");
                    }
                });
    }

    @Accessors(chain = true)
    @Setter
    private static class ComposedTickets {
        final TicketHolder ticketHolder;
        final BigInteger totalTickets;
        final BigInteger playerCount;

        private ComposedTickets(TicketHolder ticketHolder, BigInteger totalTickets) {
            this.ticketHolder = ticketHolder;
            this.totalTickets = totalTickets;
            this.playerCount = null;
        }

        private ComposedTickets(TicketHolder ticketHolder, BigInteger totalTickets, BigInteger playerCount) {
            this.ticketHolder = ticketHolder;
            this.totalTickets = totalTickets;
            this.playerCount = playerCount;
        }
    }
}
