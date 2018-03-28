package io.lastwill.eventscan.events;

import io.lastwill.eventscan.events.contract.ContractEvent;
import io.lastwill.eventscan.model.Contract;
import io.mywish.scanner.model.BaseEvent;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.web3j.protocol.core.methods.response.Transaction;
import org.web3j.protocol.core.methods.response.EthBlock;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

import java.util.List;

@RequiredArgsConstructor
@Getter
public class ContractEventsEvent extends BaseEvent {
    private final Contract contract;
    private final List<ContractEvent> events;
    private final Transaction transaction;
    private final TransactionReceipt transactionReceipt;
    private final EthBlock.Block block;

    public ContractEventsEvent(Contract contract, List<ContractEvent> events, Transaction transaction, TransactionReceipt transactionReceipt, EthBlock.Block block) {
        super(networkType);
        this.contract = contract;
        this.events = events;
        this.transaction = transaction;
        this.transactionReceipt = transactionReceipt;
        this.block = block;
    }
}
