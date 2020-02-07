package io.mywish.web3.blockchain.service;

import io.mywish.blockchain.WrapperBlock;
import io.mywish.blockchain.WrapperTransaction;
import io.mywish.scanner.model.NewLastBlockEvent;
import io.mywish.scanner.services.LastBlockPersister;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.MultiValueMap;

@Slf4j
public class Web3ScannerLastBlock extends Web3Scanner {
    public Web3ScannerLastBlock(Web3Network network, LastBlockPersister lastBlockPersister, long pollingInterval) {
        super(network, lastBlockPersister, pollingInterval, 0);
    }

    @Override
    public void doPublish(WrapperBlock block, MultiValueMap<String, WrapperTransaction> addressTransactions) {
        eventPublisher.publish(new NewLastBlockEvent(network.getType(), block, addressTransactions));
    }
}
