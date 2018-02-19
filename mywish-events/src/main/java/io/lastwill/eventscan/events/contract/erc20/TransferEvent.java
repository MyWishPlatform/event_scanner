package io.lastwill.eventscan.events.contract.erc20;

import io.lastwill.eventscan.events.contract.ContractEvent;
import io.lastwill.eventscan.events.contract.ContractEventDefinition;
import lombok.Getter;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

import java.math.BigInteger;

@Getter
public class TransferEvent extends ContractEvent {
    private final String from;
    private final String to;
    private final BigInteger tokens;

    public TransferEvent(ContractEventDefinition definition, TransactionReceipt transactionReceipt, String from, String to, BigInteger tokens, String address) {
        super(definition, address, transactionReceipt);
        this.from = from;
        this.to = to;
        this.tokens = tokens;
    }
}
