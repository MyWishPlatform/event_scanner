package io.mywish.wrapper.service.log;

import com.fasterxml.jackson.databind.JsonNode;
import io.lastwill.eventscan.model.NetworkType;
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

    private final static String CREATE_TOKEN_BUILDER_SIGNATURE = "0xfad54e54b8f76b6e89b6b455eca7b7f86a1780dd4552d2b586669a81831efeb4";

    @Value("${io.lastwill.eventscan.events.builders.eos.create-token-name.testnet}")
    private String createTokenActionNameTestnet;
    @Value("${io.lastwill.eventscan.events.builders.eos.create-token-name.mainnet}")
    private String createTokenActionNameMainnet;

    // only create now
    private HashMap<String, ContractEventBuilder<?>> byAddress = new HashMap<>();

    private static final HashMap<String, String> signatureToActionName = new HashMap<String, String>() {{
        put("0xc439029223ffeb43b811c70e26388a081f73f241e7766b81b925e1a1606e6fe8", "eosio::newaccount");
        put("0x734eb4304d685e75d086f32b6d152f26dcb5b67966530fcfcb88e2236026fbdb", "eosio.token::transfer");
        put("0x49f847db0524ed54d691f44de815e9d561ce771fcfccb8629ed5c3e6c4df664e", "eosio::setcode");
    }};

    @PostConstruct
    protected void init() throws Exception {

        // define actions from config
        signatureToActionName.put(CREATE_TOKEN_BUILDER_SIGNATURE, "stub.stub::stub");

        for (ContractEventBuilder<?> eventBuilder : builders) {
            String signature = eventBuilder.getDefinition().getSignature();
            if (signature.equals(CREATE_TOKEN_BUILDER_SIGNATURE)) {
                byAddress.put(createTokenActionNameMainnet, eventBuilder);
                log.info("Add EOS event builder {} => {}.", createTokenActionNameMainnet, eventBuilder.getClass().getSimpleName());
                byAddress.put(createTokenActionNameTestnet, eventBuilder);
                log.info("Add EOS event builder {} => {}.", createTokenActionNameTestnet, eventBuilder.getClass().getSimpleName());
                signatureToActionName.remove(CREATE_TOKEN_BUILDER_SIGNATURE);
                continue;
            }
            String actionName = signatureToActionName.remove(signature);
            if (actionName == null) {
                continue;
            }
            if (byAddress.containsKey(actionName)) {
                throw new Exception("Duplicate builder " + eventBuilder.getClass() + " with name (" + signature + ") " + actionName + ".");
            }

            byAddress.put(actionName, eventBuilder);
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
        ContractEventBuilder builder = byAddress.get(output.getAddress());
        if (builder == null) {
            log.warn("Unhandled event {}.", output.getAddress());
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
            return builder.build(output.getAccount(), args);
        }
        catch (Exception ex) {
            log.warn("Exception during parsing the event {}.", output.getAddress(), ex);
            return null;
        }
    }
}
