package io.mywish.web3.blockchain.builders.investmentPool;

import io.lastwill.eventscan.events.model.contract.investmentPool.ClaimRefundEvent;
import io.mywish.web3.blockchain.builders.Web3ContractEventBuilder;
import io.mywish.web3.blockchain.model.Web3ContractEventDefinition;
import io.mywish.web3.blockchain.model.WrapperType;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import org.web3j.abi.datatypes.Uint;

import java.math.BigInteger;
import java.util.Collections;
import java.util.List;

@Component
@NoArgsConstructor
public class ClaimRefundEventBuilder extends Web3ContractEventBuilder<ClaimRefundEvent> {
    private final static Web3ContractEventDefinition definition = new Web3ContractEventDefinition(
            "ClaimRefund",
            Collections.singletonList(
                    WrapperType.create(Uint.class, false)
            )
    );


    @Override
    public ClaimRefundEvent build(String address, List<Object> values) {
        return new ClaimRefundEvent(
                definition,
                address,
                (BigInteger) values.get(0)
        );
    }

    @Override
    public Web3ContractEventDefinition getDefinition() {
        return definition;
    }
}
