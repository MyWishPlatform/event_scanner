package io.lastwill.eventscan.services;

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
    public void onNewBlock(NewBlockEvent newBlockEvent) {
        List<Transaction> transactions = newBlockEvent.getTransactionsByAddress().getOrDefault(crowdsaleAddress, Collections.emptyList());

        if (!transactions.isEmpty()) {
            log.info("Transactions {} from crowdsale {} detected in block {}.", transactions.size(), crowdsaleAddress, newBlockEvent.getBlock().getNumber());
        }
        for (Transaction transaction: transactions) {
            bot.onInvestment(transaction.getFrom(), transaction.getValue());
        }
    }
}
