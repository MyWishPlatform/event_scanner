package io.mywish.wrapper.service.log;

import com.fasterxml.jackson.databind.JsonNode;
import io.mywish.wrapper.ContractEvent;
import io.mywish.wrapper.ContractEventBuilder;
import io.mywish.wrapper.model.output.WrapperOutputEos;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.*;

@Component
@Slf4j
public class WrapperLogEosService {
    @Autowired
    private List<ContractEventBuilder<?>> builders = new ArrayList<>();

    // only create now
    private HashMap<String, ContractEventBuilder<?>> byActionName = new HashMap<>();

    private static final HashMap<String, String> signatureToActionName = new HashMap<String, String>() {{
        put("0xfad54e54b8f76b6e89b6b455eca7b7f86a1780dd4552d2b586669a81831efeb4", "create"); // CreateTokenEvent
        put("0xc439029223ffeb43b811c70e26388a081f73f241e7766b81b925e1a1606e6fe8", "newaccount");
        put("0x734eb4304d685e75d086f32b6d152f26dcb5b67966530fcfcb88e2236026fbdb", "transfer");
        put("0x49f847db0524ed54d691f44de815e9d561ce771fcfccb8629ed5c3e6c4df664e", "setcode");
        put("0x5daa87a0e9463431830481fd4b6e3403442dfb9a12b9c07597e9f61d50b633c8", "init");
        put("0x5702595fbe7aaf5e94baa93c3f0179c3c82dbfbc2e6386ac3cca8e87457fce35", "setfinish");
        put("0xb2860396c193df8f7dcc188ad8c74f6cbd69e24c025830008b224906f94d9455", "setstart");
    }};

    @PostConstruct
    protected void init() throws Exception {
        for (ContractEventBuilder<?> eventBuilder : builders) {
            String signature = eventBuilder.getDefinition().getSignature();
            String actionName = signatureToActionName.remove(signature);
            if (actionName == null) {
                continue;
            }
            if (byActionName.containsKey(actionName)) {
                throw new Exception("Duplicate builder " + eventBuilder.getClass() + " with name (" + signature + ") " + actionName + ".");
            }

            byActionName.put(actionName, eventBuilder);
            log.info("Add EOS event builder {} => {}.", actionName, eventBuilder.getClass().getSimpleName());
        }

        if (!signatureToActionName.isEmpty()) {
            signatureToActionName.forEach((sig, name) -> {
                log.error("There is not bind event builder {} with signature {}.", name, sig);
            });
            throw new Exception("There is " + signatureToActionName.size() + " builders which is not bind. See previous error for more info.");
        }
    }

    public ContractEvent build(WrapperOutputEos output) {
        // address is the same like <account>::<method name>
        ContractEventBuilder builder = byActionName.get(output.getName());
        if (builder == null) {
            log.warn("Unhandled event {}::{}.", output.getAddress(), output.getName());
            return null;
        }
        try {
            int argsCount = builder.getDefinition().getTypes().size();
            List<Object> args = new ArrayList<>(argsCount);
            Iterator<JsonNode> iterator = output.getActionArguments().iterator();
            for (int i = 0; i < argsCount; i ++) {
                String value = iterator.next().textValue();
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
