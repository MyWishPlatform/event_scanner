package io.mywish.eos.blockchain.services;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.lastwill.eventscan.model.NetworkProviderType;
import io.mywish.blockchain.ContractEvent;
import io.mywish.blockchain.ContractEventDefinition;
import io.mywish.eos.blockchain.builders.ActionEventBuilder;
import io.mywish.eos.blockchain.model.EosActionAccountDefinition;
import io.mywish.eos.blockchain.model.EosActionFieldsDefinition;
import io.mywish.eos.blockchain.model.WrapperOutputEos;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@Component
@Slf4j
public class WrapperLogEosService {
    private final static String SEPARATOR = "::";
    @Autowired
    private List<ActionEventBuilder<?>> builders = new ArrayList<>();

    // only create now
    private HashMap<String, ActionEventBuilder<?>> byCumulativeKey = new HashMap<>();

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
        for (ActionEventBuilder<?> eventBuilder : builders) {
            if (eventBuilder.getNetworkProviderType() != NetworkProviderType.EOS) {
                return;
            }
            String key = buildKey(eventBuilder.getDefinition());
            if (byCumulativeKey.containsKey(key)) {
                throw new Exception("Duplicate builder " + eventBuilder.getClass() + " with key " + key + ".");
            }

            byCumulativeKey.put(key, eventBuilder);
            log.info("Add EOS event builder {} => {}.", key, eventBuilder.getClass().getSimpleName());
        }
    }

    public ContractEvent build(WrapperOutputEos output) {

        ActionEventBuilder builder = findBuilder(output);
        if (builder == null) {
            return null;
        }
        try {
            return builder.build(output.getAddress(), (ObjectNode) output.getActionArguments());
        }
        catch (Exception ex) {
            log.warn("Exception during parsing the event {}.", output.getAddress(), ex);
            return null;
        }
    }

    private ActionEventBuilder findBuilder(WrapperOutputEos output) {
        if (!output.getActionArguments().isObject()) {
            return null;
        }
        String fullKey = buildFullKey(output);
        ActionEventBuilder builder = byCumulativeKey.get(fullKey);
        if (builder != null) {
            return builder;
        }
        String key = buildKey(output);
        builder = byCumulativeKey.get(key);
        if (builder != null) {
            return builder;
        }

        builder = byCumulativeKey.get(output.getName());
        if (builder != null) {
            return builder;
        }
//        log.warn("Unhandled event {}::{}({}).", output.getAddress(), output.getName(), buildFullKey(output));
        return null;
    }

    private static String buildKey(ContractEventDefinition definition) {
        if (definition instanceof EosActionFieldsDefinition) {
            return Stream.concat(Stream.of(definition.getName()), ((EosActionFieldsDefinition) definition).getFields().stream())
                    .collect(Collectors.joining(SEPARATOR));
        }
        if (definition instanceof EosActionAccountDefinition) {
            return ((EosActionAccountDefinition) definition).getAccount() + SEPARATOR + definition.getName();
        }
        return definition.getName();
    }

    private static String buildKey(WrapperOutputEos output) {
        return output.getAddress() + SEPARATOR + output.getName();
    }

    private static String buildFullKey(WrapperOutputEos output) {
        ObjectNode params = (ObjectNode) output.getActionArguments();
        return Stream.concat(Stream.of(output.getName()), StreamSupport.stream(
                Spliterators.spliteratorUnknownSize(params.fieldNames(), Spliterator.ORDERED),
                false
        ))
                .collect(Collectors.joining(SEPARATOR));
    }
}
