package io.lastwill.eventscan.services.builders.erc20;

import io.mywish.wrapper.WrapperTransactionReceipt;
import io.mywish.wrapper.WrapperType;
import io.mywish.wrapper.ContractEventDefinition;
import io.lastwill.eventscan.events.contract.erc20.TransferEvent;
import io.mywish.wrapper.ContractEventBuilder;
import lombok.Getter;
import org.springframework.stereotype.Component;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Uint;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;

@Component
@Getter
public class TransferEventBuilder extends ContractEventBuilder<TransferEvent> {
    private final ContractEventDefinition definition = new ContractEventDefinition(
            "Transfer",
            Arrays.asList(
                    WrapperType.create(Address.class, true),
                    WrapperType.create(Address.class, true),
                    WrapperType.create(Uint.class, false)
            )
    );

    @Override
    public TransferEvent build(WrapperTransactionReceipt transactionReceipt, String address, List<Object> values) {
        return new TransferEvent(
                definition,
                transactionReceipt,
                (String) values.get(0),
                (String) values.get(1),
                (BigInteger) values.get(2),
                address
        );
    }
}
