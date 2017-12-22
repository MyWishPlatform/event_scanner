package io.lastwill.eventscan.services;

import io.lastwill.eventscan.events.ContractCreatedEvent;
import io.lastwill.eventscan.events.UserPaymentEvent;
import io.lastwill.eventscan.model.Contract;
import io.lastwill.eventscan.model.Product;
import io.mywish.bot.service.MyWishBot;
import io.mywish.scanner.NewBlockEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.web3j.protocol.core.methods.response.Transaction;

import java.util.Collections;
import java.util.List;

@Slf4j
@Component
@ConditionalOnBean(MyWishBot.class)
public class BotIntegration {
    @Autowired
    private MyWishBot bot;

    @Value("${io.lastwill.eventscan.contract.crowdsale-address}")
    private String crowdsaleAddress;

    @EventListener
    public void onNewBlock(final NewBlockEvent newBlockEvent) {
        List<Transaction> transactions = newBlockEvent.getTransactionsByAddress().getOrDefault(crowdsaleAddress, Collections.emptyList());

        if (!transactions.isEmpty()) {
            log.info("Transactions {} from crowdsale {} detected in block {}.", transactions.size(), crowdsaleAddress, newBlockEvent.getBlock().getNumber());
        }
        for (Transaction transaction: transactions) {
            bot.onInvestment(transaction.getFrom(), transaction.getValue());
        }
    }

    @EventListener
    public void onContractCrated(final ContractCreatedEvent contractCreatedEvent) {
        final Contract contract = contractCreatedEvent.getContract();
        final Product product = contract.getProduct();
        if (contractCreatedEvent.isSuccess()) {
            bot.onContract(contract.getId(), product.getCost(), contract.getAddress());
        }
        else {
            bot.onContractFailed(contract.getId(), contract.getTxHash());
        }
    }

    @EventListener
    public void onOwnerBalanceChanged(final UserPaymentEvent event) {
        final Product product = event.getUserProfile();
        bot.onBalance(product.getId(), event.getAmount(), product.getOwnerAddress());
    }
}
