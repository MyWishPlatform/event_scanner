package io.lastwill.eventscan.events;

import io.lastwill.eventscan.model.Contract;
import io.mywish.scanner.model.BaseEvent;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.web3j.protocol.core.methods.response.EthBlock;
import org.web3j.protocol.core.methods.response.Transaction;

@Getter
@RequiredArgsConstructor
public class ContractCreatedEvent extends BaseEvent {
    private final Contract contract;
    private final Transaction transaction;
    private final EthBlock.Block block;
    private final boolean isSuccess;

    public ContractCreatedEvent(Contract contract, Transaction transaction, EthBlock.Block block, boolean isSuccess) {
        super(networkType);
        this.contract = contract;
        this.transaction = transaction;
        this.block = block;
        this.isSuccess = isSuccess;
    }
}
