package io.lastwill.eventscan.events.builders.investmentPool;

import io.lastwill.eventscan.events.model.contract.investmentPool.ClaimRefundEvent;
import io.mywish.wrapper.ContractEventBuilder;
import io.mywish.wrapper.ContractEventDefinition;
import io.mywish.wrapper.WrapperType;
import org.springframework.stereotype.Component;
import org.web3j.abi.datatypes.Uint;

import java.math.BigInteger;
import java.util.Collections;
import java.util.List;

@Component
public class ClaimRefundEventBuilder extends ContractEventBuilder<ClaimRefundEvent> {
    private final static ContractEventDefinition definition = new ContractEventDefinition(
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
    public ContractEventDefinition getDefinition() {
        return definition;
    }
}
