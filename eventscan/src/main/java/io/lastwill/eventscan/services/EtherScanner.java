package io.lastwill.eventscan.services;

import io.lastwill.eventscan.events.NewBlockEvent;
import io.lastwill.eventscan.events.NewTransactionEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.MultiValueMap;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.Response;
import org.web3j.protocol.core.methods.response.EthBlock;
import org.web3j.protocol.core.methods.response.Transaction;

import javax.annotation.PostConstruct;
import java.util.HashMap;

@Slf4j
@Component
public class EtherScanner {
    @Autowired
    private Web3j web3j;

    @Autowired
    private EventPublisher eventPublisher;

    @PostConstruct
    protected void init() {
        web3j.blockObservable(true)
                .subscribe(this::processBlock);
    }

    private void processBlock(EthBlock ethBlock) {
        if (ethBlock == null) {
            log.warn("Null block message received! Skip it.");
            return;
        }
        if (ethBlock.hasError()) {
            Response.Error error = ethBlock.getError();
            log.warn("Block with error with code {}, message {} and data {}. Skip it.", error.getCode(), error.getMessage(), error.getData());
            return;
        }
        EthBlock.Block block = ethBlock.getBlock();
        if (block == null) {
            log.warn("Block message has null block! Skip it.");
            return;
        }

        MultiValueMap<String, Transaction> addressTransactions = CollectionUtils.toMultiValueMap(new HashMap<>());

        block.getTransactions()
                .stream()
                .map(result -> (EthBlock.TransactionObject) result)
                .map(EthBlock.TransactionObject::get)
                .forEach(transaction -> {
                    addressTransactions.add(transaction.getFrom(), transaction);
                    addressTransactions.add(transaction.getTo(), transaction);
                    eventPublisher.publish(new NewTransactionEvent(block, transaction));
                });

        eventPublisher.publish(new NewBlockEvent(block, addressTransactions));
    }
}
