package io.lastwill.eventscan.events.builders.erc223;

import io.lastwill.eventscan.events.model.contract.erc223.TransferEvent;
import io.mywish.wrapper.ContractEventBuilder;
import io.mywish.wrapper.ContractEventDefinition;
import io.mywish.wrapper.WrapperType;
import lombok.Getter;
import org.springframework.stereotype.Component;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.DynamicBytes;
import org.web3j.abi.datatypes.Uint;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;

@Component
@Getter
public class TransferERC223EventBuilder extends ContractEventBuilder<TransferEvent> {
    private final ContractEventDefinition definition = new ContractEventDefinition(
            "Transfer",
            Arrays.asList(
                    WrapperType.create(Address.class, true),
                    WrapperType.create(Address.class, true),
                    WrapperType.create(Uint.class, true),
                    WrapperType.create(DynamicBytes.class, false)
            )
    );

    @Override
    public TransferEvent build(String address, List<Object> values) {
        return new TransferEvent(
                definition,
                (String) values.get(0),
                (String) values.get(1),
                (BigInteger) values.get(2),
                (byte[]) values.get(3),
                address
        );
    }
}
