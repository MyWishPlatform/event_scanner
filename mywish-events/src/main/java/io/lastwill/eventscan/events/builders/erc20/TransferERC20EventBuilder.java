package io.lastwill.eventscan.events.builders.erc20;

import io.lastwill.eventscan.events.builders.TypeHelper;
import io.mywish.wrapper.WrapperType;
import io.mywish.wrapper.ContractEventDefinition;
import io.lastwill.eventscan.events.model.contract.erc20.TransferEvent;
import io.mywish.wrapper.ContractEventBuilder;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Uint;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;

@Component
@Getter
public class TransferERC20EventBuilder extends ContractEventBuilder<TransferEvent> {
    @Autowired
    private TypeHelper typeHelper;

    private final ContractEventDefinition definition = new ContractEventDefinition(
            "Transfer",
            Arrays.asList(
                    WrapperType.create(Address.class, true),
                    WrapperType.create(Address.class, true),
                    WrapperType.create(Uint.class, false)
            )
    );

    @Override
    public TransferEvent build(String address, List<Object> values) {
        return new TransferEvent(
                definition,
                (String) values.get(0),
                (String) values.get(1),
                typeHelper.toBigInteger(values.get(2)),
                address
        );
    }
}
