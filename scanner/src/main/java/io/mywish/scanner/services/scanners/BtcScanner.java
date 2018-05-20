package io.mywish.scanner.services.scanners;

import com.neemre.btcdcli4j.core.client.BtcdClient;
import io.mywish.wrapper.WrapperBlock;
import io.mywish.wrapper.WrapperTransaction;
import io.mywish.scanner.model.NewBlockEvent;
import io.mywish.scanner.services.LastBlockPersister;
import io.mywish.scanner.services.Scanner;
import io.mywish.wrapper.networks.BtcNetwork;
import lombok.extern.slf4j.Slf4j;
import org.bitcoinj.core.NetworkParameters;
import org.springframework.util.CollectionUtils;
import org.springframework.util.MultiValueMap;
import java.util.HashMap;

@Slf4j
public class BtcScanner extends Scanner {
    private final NetworkParameters networkParameters;
    private final BtcdClient client;

    public BtcScanner(BtcNetwork network, LastBlockPersister lastBlockPersister, NetworkParameters networkParameters, Long pollingInterval, Integer commitmentChainLength) {
        super(network, lastBlockPersister, pollingInterval, commitmentChainLength);
        this.client = network.getBtcdClient();
        this.networkParameters = networkParameters;
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
                        addressTransactions.add(output.getAddress(), transaction);
                    });
//                    eventPublisher.publish(new NewTransactionEvent(networkType, block, output));
                });

        eventPublisher.publish(new NewBlockEvent(network.getType(), block, addressTransactions));
    }
}
