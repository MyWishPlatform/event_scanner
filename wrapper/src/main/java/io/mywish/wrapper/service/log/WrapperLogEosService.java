package io.mywish.wrapper.service.log;

import com.fasterxml.jackson.databind.JsonNode;
import io.mywish.eoscli4j.model.TransactionAction;
import io.mywish.wrapper.ContractEvent;
import io.mywish.wrapper.ContractEventBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class WrapperLogEosService {
    @Autowired
    private List<ContractEventBuilder<?>> builders = new ArrayList<>();

    // only create now
    private ContractEventBuilder createEventBuilder;

    @PostConstruct
    protected void init() throws Exception {
        for (ContractEventBuilder<?> eventBuilder : builders) {
            if ("Create".equals(eventBuilder.getDefinition().getName())) {
                createEventBuilder = eventBuilder;
                break;
            }
        }
    }

    // TODO: extend functional
    public ContractEvent build(TransactionAction action) {
        String name = action.getName();
/*        ContractEventBuilder<?> builder = buildersByName.get(name);
        if (builder == null) {
            log.warn("There is not builder for EOS event with name {}.", name);
            return null;
        }*/
        // only create now
        if (!"create".equals(name)) return null;
        List<Object> args = new ArrayList<>();
        JsonNode node = action.getData();
        String issuer = node.get("issuer").asText();
        String supply = node.get("maximum_supply").asText();
        System.out.println(issuer + ": " + supply);
        args.add(issuer);
        args.add(supply);
        return createEventBuilder.build(action.getAccount(), args);
    }
}
