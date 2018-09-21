package io.lastwill.eventscan.events.builders.erc223;

import io.lastwill.eventscan.events.builders.TypeHelper;
import io.lastwill.eventscan.events.model.contract.erc223.Erc223TransferEvent;
import io.mywish.blockchain.ContractEventBuilder;
import io.mywish.blockchain.ContractEventDefinition;
import io.mywish.blockchain.WrapperType;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.DynamicBytes;
import org.web3j.abi.datatypes.Uint;

import java.util.Arrays;
import java.util.List;

@Component
@Getter
public class TransferERC223EventBuilder extends ContractEventBuilder<Erc223TransferEvent> {
    @Autowired
    private TypeHelper typeHelper;

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
    public Erc223TransferEvent build(String address, List<Object> values) {
        return new Erc223TransferEvent(
                definition,
                (String) values.get(0),
                (String) values.get(1),
                typeHelper.toBigInteger(values.get(2)),
                typeHelper.toBytesArray(values.get(3)),
                address
        );
    }
}
