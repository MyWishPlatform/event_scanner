package io.mywish.scanner.services.scanners;

import io.mywish.wrapper.WrapperBlock;
import io.mywish.wrapper.WrapperTransaction;
import io.mywish.scanner.model.NewBlockEvent;
import io.mywish.scanner.services.LastBlockPersister;
import io.mywish.scanner.services.Scanner;
import io.mywish.wrapper.networks.NeoNetwork;
import io.mywish.wrapper.transaction.WrapperTransactionNeo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import org.springframework.util.MultiValueMap;
import java.util.HashMap;

@Slf4j
public class NeoScanner extends Scanner {
    public NeoScanner(NeoNetwork network, LastBlockPersister lastBlockPersister, Long pollingInterval, Integer commitmentChainLength) {
        super(network, lastBlockPersister, pollingInterval, commitmentChainLength);
    }

    @Override
    protected void processBlock(WrapperBlock block) {
        log.info("{}: new block received {} ({})", network.getType(), block.getNumber(), block.getHash());

        MultiValueMap<String, WrapperTransaction> addressTransactions = CollectionUtils.toMultiValueMap(new HashMap<>());

        if (block.getTransactions() == null) {
            log.warn("{}: block {} has no transactions.", network.getType(), block.getNumber());
            return;
        }
        block.getTransactions()
                .forEach(transaction -> {
                    transaction.getOutputs().forEach(output -> {
//                        Script script;
//                        try {
//                            script = output.getScriptPubKey();
//                        }
//                        catch (ScriptException ex) {
//                            log.warn("Skip output with script error: ", output, ex);
//                            return;
//                        }
//                        if (!script.isSentToAddress() && !script.isPayToScriptHash() && !script.isSentToRawPubKey()) {
//                            log.debug("Skip output with not appropriate script {}.", script);
//                            return;
//                        }
                        addressTransactions.add(output.getAddress(), transaction);
                        ((WrapperTransactionNeo) transaction).getContracts().forEach(contract -> addressTransactions.add(contract, transaction));
                    });
//                    eventPublisher.publish(new NewTransactionEvent(networkType, block, output));
                });
        eventPublisher.publish(new NewBlockEvent(network.getType(), block, addressTransactions));
    }
}
