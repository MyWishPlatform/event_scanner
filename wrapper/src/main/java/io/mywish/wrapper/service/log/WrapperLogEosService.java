package io.mywish.wrapper.service.log;

import io.mywish.eoscli4j.model.TransactionAction;
import io.mywish.wrapper.ContractEvent;
import io.mywish.wrapper.ContractEventBuilder;
import io.mywish.wrapper.ContractEventDefinition;
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

    private final Map<String, ContractEventBuilder<?>> buildersByName = new HashMap<>();

    @PostConstruct
    protected void init() throws Exception {
        for (ContractEventBuilder<?> eventBuilder : builders) {
            String name = eventBuilder.getDefinition().getName();
            if (buildersByName.containsKey(name)) {
                throw new Exception("Duplicate builder " + eventBuilder.getClass() + " with name " + name);
            }
            log.info("Added builder {} for event with name {}.", eventBuilder.getClass().getSimpleName(), name);
            buildersByName.put(name, eventBuilder);
        }
    }

    public ContractEvent build(TransactionAction action) {
        String name = action.getName();
        ContractEventBuilder<?> builder = buildersByName.get(name);
        if (builder == null) {
            log.warn("There is not builder for EOS event with name {}.", name);
            return null;
        }
        ContractEventDefinition eventDefinition = builder.getDefinition();
    }
}
