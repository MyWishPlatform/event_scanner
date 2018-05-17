package io.lastwill.eventscan.events;

import io.lastwill.eventscan.events.contract.ContractEvent;
import io.lastwill.eventscan.model.Contract;
import io.mywish.scanner.WrapperBlock;
import io.mywish.scanner.WrapperTransaction;
import io.mywish.scanner.WrapperTransactionReceipt;
import io.mywish.scanner.model.BaseEvent;
import io.mywish.scanner.model.NetworkType;
import lombok.Getter;
import org.web3j.protocol.core.methods.response.Transaction;
import org.web3j.protocol.core.methods.response.EthBlock;

import java.util.List;

@Getter
public class ContractEventsEvent extends BaseEvent {
    private final Contract contract;
    private final List<ContractEvent> events;
    private final WrapperTransaction transaction;
    private final WrapperTransactionReceipt transactionReceipt;
    private final WrapperBlock block;

    public ContractEventsEvent(NetworkType networkType, Contract contract, List<ContractEvent> events, WrapperTransaction transaction, WrapperTransactionReceipt transactionReceipt, WrapperBlock block) {
        super(networkType);
        this.contract = contract;
        this.events = events;
        this.transaction = transaction;
        this.transactionReceipt = transactionReceipt;
        this.block = block;
    }
}
