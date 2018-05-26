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

    public WrapperLog build(Event event, ContractEventDefinition definition) {
        String name = ethNametoNeoName.get(event.getName());
        if (name == null) {
            return null;
        }
        String contract = event.getContract();
        List<Object> args = WrapperType.argsFromString(event.getArguments(), definition.getTypes());
        return new WrapperLog(contract, name, args);
    }
}
