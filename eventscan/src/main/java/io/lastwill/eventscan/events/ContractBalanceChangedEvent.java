package io.lastwill.eventscan.events;

import io.lastwill.eventscan.model.Contract;
import lombok.Getter;
import org.web3j.protocol.core.methods.response.EthBlock;

import java.math.BigInteger;

@Getter
public class ContractBalanceChangedEvent extends BalanceChangedEvent {
    private final Contract contract;
    public ContractBalanceChangedEvent(EthBlock.Block block, Contract contract, BigInteger amount, BigInteger balance) {
        super(block, contract.getAddress(), amount, balance);
        this.contract = contract;
    }
}
