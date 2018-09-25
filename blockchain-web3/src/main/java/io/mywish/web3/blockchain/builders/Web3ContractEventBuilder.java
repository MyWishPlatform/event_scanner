package io.mywish.web3.blockchain.builders;

import io.lastwill.eventscan.model.NetworkProviderType;
import io.mywish.blockchain.ContractEvent;
import io.mywish.blockchain.ContractEventBuilder;
import io.mywish.web3.blockchain.model.Web3ContractEventDefinition;

public abstract class Web3ContractEventBuilder<T extends ContractEvent> extends ContractEventBuilder<T> {
    protected Web3ContractEventBuilder() {
        super(NetworkProviderType.WEB3);
    }

    @Override
    public abstract Web3ContractEventDefinition getDefinition();
}
