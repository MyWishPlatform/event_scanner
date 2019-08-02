package io.mywish.waves.blockchain.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import io.lastwill.eventscan.model.NetworkProviderType;
import io.mywish.blockchain.ContractEvent;
import io.mywish.waves.blockchain.builders.WavesEventBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.*;

@Slf4j
@Component
public class WrapperLogWavesService {
    @Autowired
    private List<WavesEventBuilder<?>> builders = new ArrayList<>();
    private Map<String, WavesEventBuilder<?>> buildersByName = new HashMap<>();

    @PostConstruct
    protected void init() throws Exception {
        for (WavesEventBuilder<?> eventBuilder : builders) {
            if (eventBuilder.getNetworkProviderType() != NetworkProviderType.WAVES) {
                return;
            }
            String key = eventBuilder.getDefinition().getName();
            if (buildersByName.containsKey(key)) {
                throw new Exception("Duplicate builder " + eventBuilder.getClass() + " with key " + key + ".");
            }

            buildersByName.put(key, eventBuilder);
            log.info("Add Waves event builder {} => {}.", key, eventBuilder.getClass().getSimpleName());
        }
    }

    public List<? extends ContractEvent> build(JsonNode jsonNode) {
        int type = jsonNode.get("type").asInt();
        if (type != 16) {
            return Collections.emptyList();
        }

        String address = jsonNode.get("dApp").asText();
        String functionName = jsonNode.get("call").get("function").asText();
        ArrayNode args = (ArrayNode) jsonNode.get("call").get("args");

        WavesEventBuilder<?> builder = buildersByName.get(functionName);
        if (builder == null) {
            log.warn("There is no builder for Waves event {}.", functionName);
            return null;
        }

        return builder.build(address, args);
    }
}
