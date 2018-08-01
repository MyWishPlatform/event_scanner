package io.mywish.scanner.services.scanners;

import io.mywish.eoscli4j.model.Transaction;
import io.mywish.scanner.model.NewBlockEvent;
import io.mywish.scanner.services.LastBlockPersister;
import io.mywish.scanner.services.Scanner;
import io.mywish.wrapper.WrapperBlock;
import io.mywish.wrapper.WrapperTransaction;
import io.mywish.wrapper.networks.EosNetwork;
import io.mywish.wrapper.transaction.WrapperTransactionEos;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import org.springframework.util.MultiValueMap;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.HashMap;

@Slf4j
public class EosScanner extends Scanner {
    private final Runnable blockReceiver = () -> {
        try {
            ((EosNetwork) network).subscribe(network.getLastBlock(), block -> {
                lastBlockPersister.saveLastBlock(block.getNumber());
                processBlock(block);
            });
        } catch (Exception e) {
            // TODO: exception handling
            e.printStackTrace();
        }
    };

    public EosScanner(EosNetwork network, LastBlockPersister lastBlockPersister) {
        super(network, lastBlockPersister);
        this.setWorker(blockReceiver);
    }

    @Override
    protected void processBlock(WrapperBlock block) {
        MultiValueMap<String, WrapperTransaction> addressTransactions = CollectionUtils.toMultiValueMap(new HashMap<>());
        block.getTransactions().forEach(tx -> {
            Transaction eosTx = ((WrapperTransactionEos)tx).getNativeTransaction();
            eosTx.getTrx().getTransaction().getActions().forEach(action -> {
                addressTransactions.set(action.getAccount(), tx);
            });
        });
        eventPublisher.publish(new NewBlockEvent(network.getType(), block, addressTransactions));
        System.out.println(block.getNumber());
    }

    @PostConstruct
    @Override
    protected void open() {
        lastBlockPersister.open();
    }

    @PreDestroy
    @Override
    protected void close() {
        try {
            lastBlockPersister.close();
        }
        catch (Exception e) {
            log.warn("Persister for {} closing failed.", network.getType(), e);
        }
    }
}
