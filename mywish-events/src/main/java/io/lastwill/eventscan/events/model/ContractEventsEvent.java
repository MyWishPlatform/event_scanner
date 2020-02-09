package io.lastwill.eventscan.events.model;

import io.mywish.blockchain.*;
import io.lastwill.eventscan.model.Contract;
import io.lastwill.eventscan.model.NetworkType;
import lombok.Getter;

import java.util.List;

@Getter
public class ContractEventsEvent extends BaseEvent {
    private final Contract contract;
    private final List<ContractEvent> events;
    private final WrapperTransaction transaction;
    private final WrapperTransactionReceipt transactionReceipt;
    private final WrapperBlock block;
    private long blocksConfirmed;

    public ContractEventsEvent(NetworkType networkType, Contract contract, List<ContractEvent> events, WrapperTransaction transaction, WrapperTransactionReceipt transactionReceipt, WrapperBlock block ) {
        super(networkType);
        this.contract = contract;
        this.events = events;
        this.transaction = transaction;
        this.transactionReceipt = transactionReceipt;
        this.block = block;

    }


   // public Long getBlocksConfirmed() {
       // return blocksConfirmed;
   // }

    public void setBlocksConfirmed(Long blocksConfirmed) {
        this.blocksConfirmed = blocksConfirmed;
    }
}
