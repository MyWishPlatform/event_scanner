package io.lastwill.eventscan.events.model;

import io.mywish.blockchain.*;
import io.lastwill.eventscan.model.Contract;
import io.lastwill.eventscan.model.NetworkType;
import lombok.Getter;

import java.util.List;

@Getter
public class ContractEventsEventWithConfirmation extends BaseEvent {
    private final Long blocksConfirmed;
    private final String txHash;


    public ContractEventsEventWithConfirmation(NetworkType networkType, String txHash, Long blocksConfirmed ) {
        super(networkType);
        this.txHash = txHash;
        this.blocksConfirmed = blocksConfirmed;

    }


}
