package io.mywish.btc.blockchain.services;

import io.mywish.blockchain.WrapperBlock;
import io.mywish.blockchain.WrapperTransaction;
import io.mywish.scanner.model.NewBlockEvent;
import io.mywish.scanner.services.LastBlockPersister;
import io.mywish.scanner.services.ScannerPolling;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import org.springframework.util.MultiValueMap;

import java.util.HashMap;

@Slf4j
public class BtcScanner extends ScannerPolling {
    public BtcScanner(BtcNetwork network, LastBlockPersister lastBlockPersister, Long pollingInterval, Integer commitmentChainLength) {
        super(network, lastBlockPersister, pollingInterval, commitmentChainLength);
    }

    @Override
    protected void processBlock(WrapperBlock block) {
        log.info("{}: new block received {} ({})", network.getType(), block.getNumber(), block.getHash());

        MultiValueMap<String, WrapperTransaction> addressTransactions = CollectionUtils.toMultiValueMap(new HashMap<>());

        if (block.getTransactions() == null) {
            log.warn("{}: block {} has not transactions.", network.getType(), block.getNumber());
            return;
        }
        block.getTransactions()
                .forEach(transaction -> {
                    transaction.getOutputs().forEach(output -> {
                        addressTransactions.add(
                                output.getAddress(),
                                transaction
                        );
                    });
//                    eventPublisher.publish(new NewTransactionEvent(networkType, block, output));
                });

        eventPublisher.publish(new NewBlockEvent(network.getType(), block, addressTransactions));
    }
}
