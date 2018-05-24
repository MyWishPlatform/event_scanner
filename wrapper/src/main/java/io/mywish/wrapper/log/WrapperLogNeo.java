package io.mywish.wrapper.log;

import io.mywish.neocli4j.Event;
import io.mywish.wrapper.ContractEventDefinition;
import io.mywish.wrapper.WrapperLog;
import io.mywish.wrapper.WrapperType;

public class WrapperLogNeo extends WrapperLog {
    public WrapperLogNeo(Event event, ContractEventDefinition definition) {
        super(
                event.getContract(),
                event.getName(),
                WrapperType.argsFromString(event.getArguments(), definition.getTypes())
        );
    }
}
