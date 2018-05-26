package io.lastwill.eventscan.services.builders;

import io.lastwill.eventscan.events.contract.MintEvent;
import io.mywish.wrapper.ContractEventBuilder;
import io.mywish.wrapper.ContractEventDefinition;
import io.mywish.wrapper.WrapperTransactionReceipt;
import io.mywish.wrapper.WrapperType;
import lombok.Getter;
import org.springframework.stereotype.Component;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Uint;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;

@Getter
@Component
public class MintEventBuilder extends ContractEventBuilder<MintEvent> {
    private final ContractEventDefinition definition = new ContractEventDefinition(
            "Mint",
            Arrays.asList(WrapperType.create(Address.class, true), WrapperType.create(Uint.class, false))
    );

    @Override
    public MintEvent build(WrapperTransactionReceipt transactionReceipt, String address, List<Object> values) {
        return new MintEvent(definition, transactionReceipt, (String) values.get(0), (BigInteger) values.get(1), address);
    }
}
