package io.mywish.dream.service;

import io.mywish.dream.model.contracts.TicketSale;
import io.mywish.dream.exception.CreationException;
import io.mywish.dream.model.Contract;
import io.mywish.dream.repositories.ContractRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.admin.Admin;
import org.web3j.protocol.admin.methods.response.PersonalUnlockAccount;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tx.ClientTransactionManager;
import org.web3j.tx.TransactionManager;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.concurrent.CompletionStage;

@Slf4j
@Component
public class TicketService {
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

    @PostConstruct
    protected void init() throws IOException {
        transactionManager = new ClientTransactionManager(web3j, serverAddress);
        admin.personalUnlockAccount(serverAddress, serverAccountPassword) // BigInteger.valueOf(2_592_000)
                .send();
    }

    public CompletionStage<String> deploy(LocalDateTime endDate) {
        return admin.personalUnlockAccount(serverAddress, serverAccountPassword)
                .sendAsync()
                .thenCompose(personalUnlockAccount -> {
                    if (personalUnlockAccount.getResult() == null || !personalUnlockAccount.getResult()) {
                        throw new CreationException("Impossible to unlock account.");
                    }
                    return TicketSale.deploy(web3j, transactionManager, gasPrice, gasLimit, BigInteger.valueOf(endDate.toEpochSecond(ZoneOffset.UTC)))
                            .sendAsync()
                            .thenApply(ticketSale -> {
                                TransactionReceipt receipt = ticketSale.getTransactionReceipt()
                                        .orElseThrow(() -> new CreationException("Contract creation finished, but there is no receipt."));
                                if ("0".equals(receipt.getStatus())) {
                                    throw new CreationException("Transaction " + receipt.getTransactionHash() + " finished with failed status.");
                                }

                                contractRepository.save(new Contract(ticketSale.getContractAddress(), serverAddress));

                                return ticketSale.getContractAddress();
                            });
                });
    }
}
