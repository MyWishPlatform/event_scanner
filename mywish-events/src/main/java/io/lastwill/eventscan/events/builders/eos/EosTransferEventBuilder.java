package io.lastwill.eventscan.events.builders.eos;

import io.lastwill.eventscan.events.builders.TypeHelper;
import io.lastwill.eventscan.events.model.contract.eos.EosTransferEvent;
import io.mywish.blockchain.ContractEventBuilder;
import io.mywish.blockchain.ContractEventDefinition;
import io.mywish.blockchain.WrapperType;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.DynamicBytes;
import org.web3j.abi.datatypes.Utf8String;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;

@Component
@Getter
public class EosTransferEventBuilder extends ContractEventBuilder<EosTransferEvent> {
    @Autowired
    private TypeHelper typeHelper;

    private final ContractEventDefinition definition = new ContractEventDefinition(
            "eosio.token::transfer",
            Arrays.asList(
                    WrapperType.create(Address.class, true),
                    WrapperType.create(Address.class, true),
                    WrapperType.create(Utf8String.class, false),
                    WrapperType.create(DynamicBytes.class, false)
            )
    );

    @Override
    public EosTransferEvent build(String address, List<Object> values) {
        String value = (String) values.get(2);
        int spaceIndex = value.indexOf(" ");
        String amount = value.substring(0, spaceIndex);
        String symbol = value.substring(spaceIndex + 1);
        BigDecimal amountDecimals = new BigDecimal(amount);
        BigInteger amountInteger = amountDecimals.multiply(BigDecimal.valueOf(10).pow(amountDecimals.scale())).toBigInteger();

        return new EosTransferEvent(
                definition,
                (String) values.get(0),
                (String) values.get(1),
                amountInteger,
                typeHelper.toBytesArray(values.get(3)),
                symbol,
                address
        );
    }
}
