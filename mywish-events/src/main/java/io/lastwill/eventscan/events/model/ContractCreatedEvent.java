package io.lastwill.eventscan.events.model;

import io.lastwill.eventscan.model.Contract;
import io.mywish.blockchain.WrapperBlock;
import io.mywish.blockchain.WrapperTransaction;
import io.lastwill.eventscan.model.NetworkType;
import lombok.Getter;

@Getter
public class ContractCreatedEvent extends BaseEvent {
    private final Contract contract;
    private final WrapperTransaction transaction;
    private final WrapperBlock block;
    private final boolean isSuccess;

    public ContractCreatedEvent(NetworkType networkType, Contract contract, WrapperTransaction transaction, WrapperBlock block, boolean isSuccess) {
        super(networkType);
        this.contract = contract;
        this.transaction = transaction;
        this.block = block;
        this.isSuccess = isSuccess;
    }
}
