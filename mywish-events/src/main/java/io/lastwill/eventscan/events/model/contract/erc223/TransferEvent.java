package io.lastwill.eventscan.events.model.contract.erc223;

import io.mywish.wrapper.ContractEventDefinition;
import io.mywish.wrapper.WrapperTransactionReceipt;
import lombok.Getter;

import java.math.BigInteger;

@Getter
public class TransferEvent extends io.lastwill.eventscan.events.model.contract.erc20.TransferEvent {
    private final byte[] data;

    public TransferEvent(ContractEventDefinition definition, String from, String to, BigInteger tokens, byte[] data, String address) {
        super(definition, from, to, tokens, address);
        this.data = data;
    }
}
