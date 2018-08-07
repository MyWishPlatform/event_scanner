package io.mywish.wrapper.service.log;

import io.mywish.wrapper.ContractEvent;
import io.mywish.wrapper.ContractEventBuilder;
import io.mywish.wrapper.model.output.WrapperOutputEos;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Component
@Slf4j
public class WrapperLogEosService {
    @Autowired
    private List<ContractEventBuilder<?>> builders = new ArrayList<>();

    // only create now
    private HashMap<String, ContractEventBuilder<?>> byAddress = new HashMap<>();

    private static final HashMap<String, String> signatureToName = new HashMap<String, String>() {{
       put("0xc439029223ffeb43b811c70e26388a081f73f241e7766b81b925e1a1606e6fe8", "eosio::newaccount");
//       put("", "eosio::create");
    }};

    @PostConstruct
    protected void init() throws Exception {
        for (ContractEventBuilder<?> eventBuilder : builders) {
            String signature = eventBuilder.getDefinition().getSignature();
            String existingName = signatureToName.remove(signature);
            if (existingName == null) {
                continue;
            }
            String name = eventBuilder.getDefinition().getName();

            if (byAddress.containsKey(name)) {
                throw new Exception("Duplicate builder " + eventBuilder.getClass() + " with name (" + signature + ") " + name + ".");
            }

            byAddress.put(name, eventBuilder);
        }

        if (signatureToName.isEmpty()) {
            signatureToName.forEach((sig, name) -> {
                log.error("There is not bind event builder {} with signature {}.", name, sig);
            });
            throw new Exception("There is " + signatureToName.size() + " builders which is not bind. See previous error for more info.");
        }
    }

    public ContractEvent build(WrapperOutputEos output) {
        // address is the same like <account>::<method name>
        ContractEventBuilder builder = byAddress.get(output.getAddress());
        if (!byAddress.containsKey(output.getAddress())) {
            log.warn("Unhandled event {}.", output.getAddress());
            return null;
        }
        try {
            List<Object> args = new ArrayList<>(builder.getDefinition().getTypes().size());
            for (int i = 0; i < args.size(); i ++) {
                String value = output.getActionArguments().get(i).textValue();
                args.add(value);
            }
            return builder.build(output.getAddress(), args);
        }
        catch (Exception ex) {
            log.warn("Exception during parsing the event {}.", output.getAddress(), ex);
            return null;
        }
    }
}
