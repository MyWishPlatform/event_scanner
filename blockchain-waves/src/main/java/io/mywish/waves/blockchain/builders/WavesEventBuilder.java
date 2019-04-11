package io.mywish.waves.blockchain.builders;

import com.sun.istack.internal.Nullable;
import com.wavesplatform.wavesj.Transaction;
import io.lastwill.eventscan.model.NetworkProviderType;
import io.mywish.blockchain.ContractEvent;
import io.mywish.blockchain.ContractEventBuilder;
import io.mywish.blockchain.ContractEventDefinition;

import java.util.List;

public abstract class WavesEventBuilder<T extends ContractEvent> extends ContractEventBuilder<T> {
    protected WavesEventBuilder() {
        super(NetworkProviderType.WAVES);
    }

    public T build(String address, List<Object> values) {
        throw new UnsupportedOperationException("Use other method with other signature.");
    }

    @Nullable
    public abstract List<ContractEvent> build(Transaction transaction);

    @Override
    public ContractEventDefinition getDefinition() {
        throw new UnsupportedOperationException("Waves doesn't use definitions.");
    }
}
