package io.mywish.wrapper.service.log;

import io.mywish.neocli4j.Event;
import io.mywish.wrapper.ContractEventDefinition;
import io.mywish.wrapper.WrapperLog;
import io.mywish.wrapper.WrapperType;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
            return null;
        }
        ContractEventDefinition definition = definitions.get(name);
        if (definition == null) {
            return null;
        }
        String contract = event.getContract();
        List<Object> args = WrapperType.argsFromBytes(event.getArguments(), definition.getTypes());
        return new WrapperLog(contract, name, args);
    }
}
