package io.lastwill.eventscan.events.model.contract.waves;

import io.mywish.blockchain.ContractEvent;

public class WavesContractEvent extends ContractEvent {
    public WavesContractEvent(String address) {
        super(null, address);
    }
}
