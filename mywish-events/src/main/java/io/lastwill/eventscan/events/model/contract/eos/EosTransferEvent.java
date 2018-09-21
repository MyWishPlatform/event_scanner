package io.lastwill.eventscan.events.model.contract.eos;

import io.lastwill.eventscan.events.model.contract.erc223.Erc223TransferEvent;
import io.mywish.blockchain.ContractEventDefinition;
import lombok.Getter;

import java.math.BigInteger;

@Getter
public class EosTransferEvent extends Erc223TransferEvent {
    private final String symbol;

    public EosTransferEvent(ContractEventDefinition definition, String from, String to, BigInteger tokens, byte[] data, String symbol, String address) {
        super(definition, from, to, tokens, data, address);
        this.symbol = symbol;
    }
}
