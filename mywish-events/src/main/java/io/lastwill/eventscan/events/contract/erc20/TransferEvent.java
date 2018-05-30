package io.lastwill.eventscan.events.contract.erc20;

import io.mywish.wrapper.WrapperTransactionReceipt;
import io.mywish.wrapper.ContractEvent;
import io.mywish.wrapper.ContractEventDefinition;
import lombok.Getter;
import java.math.BigInteger;

@Getter
public class TransferEvent extends ContractEvent {
    private final String from;
    private final String to;
    private final BigInteger tokens;

    public TransferEvent(ContractEventDefinition definition, WrapperTransactionReceipt transactionReceipt, String from, String to, BigInteger tokens, String address) {
        super(definition, address, transactionReceipt);
        this.from = from;
        this.to = to;
        this.tokens = tokens;
    }
}
