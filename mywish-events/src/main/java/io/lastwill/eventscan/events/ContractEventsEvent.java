package io.lastwill.eventscan.events;

import io.lastwill.eventscan.model.Contract;
import io.lastwill.eventscan.model.EventValue;
import io.mywish.scanner.BaseEvent;
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
    private final List<EventValue> events;
    private final Transaction transaction;
    private final TransactionReceipt transactionReceipt;
    private final EthBlock.Block block;
}
