package io.lastwill.eventscan.events.model.contract.erc223;

import io.mywish.blockchain.ContractEventDefinition;
import lombok.Getter;

import java.math.BigInteger;

@Getter
public class Erc223TransferEvent extends io.lastwill.eventscan.events.model.contract.erc20.TransferEvent {
    private final byte[] data;

    public Erc223TransferEvent(ContractEventDefinition definition, String from, String to, BigInteger tokens, byte[] data, String address) {
        super(definition, from, to, tokens, address);
        this.data = data;
    }
}
