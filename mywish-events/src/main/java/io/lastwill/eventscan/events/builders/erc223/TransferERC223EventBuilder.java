package io.lastwill.eventscan.events.builders.erc223;

import io.lastwill.eventscan.events.model.contract.erc223.TransferEvent;
import io.mywish.wrapper.ContractEventBuilder;
import io.mywish.wrapper.ContractEventDefinition;
import io.mywish.wrapper.WrapperTransactionReceipt;
import io.mywish.wrapper.WrapperType;
import lombok.Getter;
import org.springframework.stereotype.Component;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Bytes;
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
                    WrapperType.create(Bytes.class, false)
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
                (byte[]) values.get(3),
                address
        );
    }
}
