package io.mywish.web3.blockchain.builders.tokenProtector;

import io.lastwill.eventscan.events.TypeHelper;
import io.lastwill.eventscan.events.model.contract.tokenProtector.TransactionInfoEvent;
import io.mywish.web3.blockchain.builders.Web3ContractEventBuilder;
import io.mywish.web3.blockchain.model.Web3ContractEventDefinition;
import io.mywish.web3.blockchain.model.WrapperType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Uint;

import java.util.Arrays;
import java.util.List;

@Component
@Getter
@NoArgsConstructor
public class ProtectorExecutionEventBuilder extends Web3ContractEventBuilder<TransactionInfoEvent> {
    @Autowired
    private TypeHelper typeHelper;

    private final Web3ContractEventDefinition definition = new Web3ContractEventDefinition(
            "TransactionInfo",
            Arrays.asList(
                    WrapperType.create(Address.class, false),
                    WrapperType.create(Uint.class, false)
            )
    );

    @Override
    public TransactionInfoEvent build(String address, List<Object> values) {
        return new TransactionInfoEvent(
                definition,
                address,
                (String) values.get(0),
                typeHelper.toBigInteger(values.get(1))

        );
    }
}
