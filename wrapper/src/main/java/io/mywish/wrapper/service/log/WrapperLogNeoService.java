package io.mywish.wrapper.service.log;

import io.mywish.neocli4j.Event;
import io.mywish.wrapper.ContractEventDefinition;
import io.mywish.wrapper.WrapperLog;
import io.mywish.wrapper.WrapperType;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class WrapperLogNeoService {
    public WrapperLog build(Event event, ContractEventDefinition definition) {
        String contract = event.getContract();
        String name = event.getName();
        List<Object> args = WrapperType.argsFromString(event.getArguments(), definition.getTypes());
        return new WrapperLog(contract, name, args);
    }
}
