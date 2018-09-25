package io.mywish.eos.blockchain.services;

import com.fasterxml.jackson.databind.JsonNode;
import io.lastwill.eventscan.model.NetworkProviderType;
import io.mywish.blockchain.ContractEvent;
import io.mywish.blockchain.ContractEventBuilder;
import io.mywish.blockchain.ContractEventDefinition;
import io.mywish.eos.blockchain.builders.ActionEventBuilder;
import io.mywish.eos.blockchain.model.EosActionDefinition;
import io.mywish.eos.blockchain.model.WrapperOutputEos;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.math.BigInteger;
import java.util.*;

@Component
@Slf4j
public class WrapperLogEosService {
    private final static String SEPARATOR = "::";
    @Autowired
    private List<ActionEventBuilder<?>> builders = new ArrayList<>();

    // only create now
    private HashMap<String, ContractEventBuilder<?>> byActionName = new HashMap<>();

//    private final HashSet<String> requiredActions =  new HashSet<String>() {{
//        add("create");
//        add("newaccount");
//        add("transfer");
//        add("setcode");
//        add("init");
//        add("setfinish");
//        add("setstart");
//        add("finalize");
//    }};

    @PostConstruct
    protected void init() throws Exception {
        for (ContractEventBuilder<?> eventBuilder : builders) {
            if (eventBuilder.getNetworkProviderType() != NetworkProviderType.EOS) {
                return;
            }
            String key = buildKey(eventBuilder.getDefinition());
            if (byActionName.containsKey(key)) {
                throw new Exception("Duplicate builder " + eventBuilder.getClass() + " with key " + key + ".");
            }

            byActionName.put(key, eventBuilder);
            log.info("Add EOS event builder {} => {}.", key, eventBuilder.getClass().getSimpleName());
        }
    }

    public ContractEvent build(WrapperOutputEos output) {
        ContractEventBuilder builder = byActionName.get(buildKey(output));
        if (builder == null) {
            builder = byActionName.get(output.getName());
            if (builder == null) {
                log.warn("Unhandled event {}::{}.", output.getAddress(), output.getName());
                return null;
            }
        }
        try {
            List<Object> args = new ArrayList<>();
            Iterator<JsonNode> iterator = output.getActionArguments().iterator();
            while (iterator.hasNext()) {
                JsonNode node = iterator.next();
                if (node.isBigDecimal()) {
                    args.add(node.decimalValue());
                }
                else if (node.isBigInteger()) {
                    args.add(node.bigIntegerValue());
                }
                else if (node.isInt()) {
                    args.add(BigInteger.valueOf(node.intValue()));
                }
                else if (node.isLong()) {
                    args.add(BigInteger.valueOf(node.longValue()));
                }
                else {
                    args.add(node.textValue());
                }
            }
            return builder.build(output.getAddress(), args);
        }
        catch (Exception ex) {
            log.warn("Exception during parsing the event {}.", output.getAddress(), ex);
            return null;
        }
    }

    private static String buildKey(ContractEventDefinition definition) {
        if (definition instanceof EosActionDefinition) {
            return ((EosActionDefinition) definition).getAccount() + SEPARATOR + definition.getName();
        }
        return definition.getName();
    }

    private static String buildKey(WrapperOutputEos output) {
        return output.getAddress() + SEPARATOR + output.getName();
    }
}
