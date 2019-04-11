package io.mywish.waves.blockchain.services;

import com.wavesplatform.wavesj.Transaction;
import io.lastwill.eventscan.model.NetworkProviderType;
import io.mywish.blockchain.ContractEvent;
import io.mywish.waves.blockchain.builders.WavesEventBuilder;
import io.mywish.waves.blockchain.model.WrapperTransactionWaves;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Component
public class WrapperLogWavesService {
    @Autowired
    private List<WavesEventBuilder<?>> builders = new ArrayList<>();

    public List<ContractEvent> build(WrapperTransactionWaves wrapperTransaction) {
        Transaction tx = wrapperTransaction.getTransaction();
        return builders
                .stream()
                .filter(wavesEventBuilder -> NetworkProviderType.WAVES
                        .equals(wavesEventBuilder.getNetworkProviderType()))
                .map(wavesEventBuilder -> wavesEventBuilder.build(tx))
                .flatMap(Collection::stream)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
}
