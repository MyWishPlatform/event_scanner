package io.mywish.eos.blockchain.util;

import io.mywish.blockchain.WrapperBlock;
import io.mywish.blockchain.WrapperNetwork;
import io.mywish.blockchain.WrapperOutput;
import io.mywish.blockchain.WrapperTransaction;
import io.mywish.scanner.model.NewBlockEvent;
import io.mywish.scanner.model.NewPendingTransactionsEvent;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.util.CollectionUtils;
import org.springframework.util.MultiValueMap;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Stream;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class NewBlockEventUtil {
    public static NewBlockEvent createBlockEvent(WrapperNetwork network, WrapperBlock block) {
        MultiValueMap<String, WrapperTransaction> addressTransactions = CollectionUtils.toMultiValueMap(new HashMap<>());

        block.getTransactions().forEach(tx -> {
            Stream.concat(
                    tx.getInputs().stream(),
                    tx.getOutputs().stream()
                            .map(WrapperOutput::getAddress))
                    .filter(address -> !contains(addressTransactions, address, tx))
                    .forEach(address -> addressTransactions.add(address, tx));
        });
        return new NewBlockEvent(network.getType(), block, addressTransactions);
    }

    public static NewPendingTransactionsEvent createPendingEvent(WrapperNetwork network, WrapperBlock block) {
        return new NewPendingTransactionsEvent(
                network.getType(),
                block.getTransactions()
        );
    }

    private static <K, V> boolean contains(MultiValueMap<K, V> map, K key, V value) {
        List<V> list = map.get(key);
        return list != null && list.contains(value);
    }
}
