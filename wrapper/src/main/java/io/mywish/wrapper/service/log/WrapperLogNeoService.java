package io.mywish.wrapper.service.log;

import io.mywish.neocli4j.Event;
import io.mywish.wrapper.ContractEventDefinition;
import io.mywish.wrapper.WrapperLog;
import io.mywish.wrapper.WrapperType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class WrapperLogNeoService {
    private Map<String, String> ethNametoNeoName = new HashMap<String, String>() {{
        put("transfer", "Transfer");
        put("mint", "Mint");
        put("finishMint", "MintFinished");
        put("init", "Initialized");
        put("transferOwnership", "OwnershipTransferred");
    }};

    public WrapperLog build(Event event, Map<String, ContractEventDefinition> definitions) {
        String name = ethNametoNeoName.get(event.getName());
        if (name == null) {
            log.warn("There is no corresponding NEO event for {}.", event.getName());
            return null;
        }
        ContractEventDefinition definition = definitions.get(name);
        if (definition == null) {
            log.warn("There is no corresponding NEO event definition for {}.", name);
            return null;
        }
        String contract = event.getContract();
        List<Object> args = WrapperType.argsFromBytes(event.getArguments(), definition.getTypes());
        return new WrapperLog(contract, name, args);
    }
}
