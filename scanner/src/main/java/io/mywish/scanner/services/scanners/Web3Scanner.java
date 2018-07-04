package io.mywish.scanner.services.scanners;

import io.mywish.scanner.services.PendingTransactionService;
import io.mywish.wrapper.WrapperBlock;
import io.mywish.wrapper.WrapperOutput;
import io.mywish.wrapper.WrapperTransaction;
import io.mywish.wrapper.WrapperTransactionReceipt;
import io.mywish.scanner.model.NewBlockEvent;
import io.mywish.scanner.services.LastBlockPersister;
import io.mywish.scanner.services.Scanner;
import io.mywish.wrapper.networks.Web3Network;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import org.springframework.util.MultiValueMap;

import java.util.HashMap;

@Slf4j
public class Web3Scanner extends Scanner {
    public Web3Scanner(Web3Network network, LastBlockPersister lastBlockPersister, long pollingInterval, int commitmentChainLength) {
        super(network, lastBlockPersister, pollingInterval, commitmentChainLength);
    }

    @Override
    protected void processBlock(WrapperBlock block) {
        log.info("{}: new block received {} ({})", network.getType(), block.getNumber(), block.getHash());

        MultiValueMap<String, WrapperTransaction> addressTransactions = CollectionUtils.toMultiValueMap(new HashMap<>());

        block.getTransactions()
                .forEach(transaction -> {
                    String from = transaction.getInputs().get(0);
                    WrapperOutput to = transaction.getOutputs().get(0);
                    if (from != null) {
                        addressTransactions.add(from.toLowerCase(), transaction);
                    }
                    else {
                        log.warn("Empty from field for transaction {}. Skip it.", transaction.getHash());
                    }
                    if (to != null && to.getAddress() != null) {
                        addressTransactions.add(to.getAddress().toLowerCase(), transaction);
                    }
                    else {
                        if (transaction.getCreates() != null) {
                            addressTransactions.add(transaction.getCreates().toLowerCase(), transaction);
                        }
                        else {
                            try {
                                WrapperTransactionReceipt receipt = network.getTxReceipt(transaction);
                                String contract = receipt.getContracts().get(0);
                                transaction.setCreates(contract);
                                addressTransactions.add(
                                        contract.toLowerCase(),
                                        transaction
                                );
                            }
                            catch (Exception e) {
                                log.error("Error on getting transaction {} receipt.", transaction.getHash(), e);
                                log.warn("Empty to and creates field for transaction {}. Skip it.", transaction.getHash());
                            }
                        }

                    }
//                    eventPublisher.publish(new NewTransactionEvent(network.getType(), block, transaction));
                });

        eventPublisher.publish(new NewBlockEvent(network.getType(), block, addressTransactions));
    }
}