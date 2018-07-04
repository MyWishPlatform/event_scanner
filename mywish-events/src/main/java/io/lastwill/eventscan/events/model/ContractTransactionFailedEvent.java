package io.lastwill.eventscan.events.model;

import io.lastwill.eventscan.model.Contract;
import io.lastwill.eventscan.model.NetworkType;
import io.mywish.wrapper.WrapperBlock;
import io.mywish.wrapper.WrapperTransaction;
import io.mywish.wrapper.WrapperTransactionReceipt;
import lombok.Getter;

@Getter
public class ContractTransactionFailedEvent extends BaseEvent {
    private final Contract contract;
    private final WrapperTransaction transaction;
    private final WrapperTransactionReceipt transactionReceipt;
    private final WrapperBlock block;

    public ContractTransactionFailedEvent(NetworkType networkType, Contract contract, WrapperTransaction transaction, WrapperTransactionReceipt transactionReceipt, WrapperBlock block) {
        super(networkType);
        this.contract = contract;
        this.transaction = transaction;
        this.transactionReceipt = transactionReceipt;
        this.block = block;
    }
}
