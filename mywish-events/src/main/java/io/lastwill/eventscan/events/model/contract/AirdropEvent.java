package io.lastwill.eventscan.events.model.contract;

import io.mywish.blockchain.ContractEvent;
import io.mywish.blockchain.ContractEventDefinition;
import lombok.Getter;
import lombok.ToString;

import java.math.BigInteger;
import java.util.List;

@ToString
@Getter
public class AirdropEvent extends ContractEvent {
    private final String adminAddress;
    private final String tokenAddress;
    private final String tokenSymbol;

    private final List<String> addresses;
    private final List<BigInteger> values;

    public AirdropEvent(ContractEventDefinition definition, String address, String adminAddress, String tokenAddress, String tokenSymbol, List<String> addresses, List<BigInteger> values) {
        super(definition, address);
        this.adminAddress = adminAddress;
        this.tokenAddress = tokenAddress;
        this.tokenSymbol = tokenSymbol;
        this.addresses = addresses;
        this.values = values;
    }
}
