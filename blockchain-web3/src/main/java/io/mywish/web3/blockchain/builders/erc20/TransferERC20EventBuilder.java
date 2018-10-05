package io.mywish.web3.blockchain.builders.erc20;

import io.lastwill.eventscan.events.TypeHelper;
import io.lastwill.eventscan.events.model.contract.erc20.TransferEvent;
import io.mywish.blockchain.ContractEventDefinition;
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
public class TransferERC20EventBuilder extends Web3ContractEventBuilder<TransferEvent> {
    @Autowired
    private TypeHelper typeHelper;

    private final Web3ContractEventDefinition definition = new Web3ContractEventDefinition(
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
