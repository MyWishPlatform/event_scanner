package io.mywish.waves.blockchain.builders;

import com.fasterxml.jackson.databind.node.ArrayNode;
import io.lastwill.eventscan.events.model.contract.crowdsale.FinalizedEvent;
import io.mywish.blockchain.ContractEventDefinition;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Getter
@Component
public class FinalizedWavesEventBuilder extends WavesEventBuilder<FinalizedEvent> {
    private final ContractEventDefinition definition = new ContractEventDefinition("finalize");

    @Override
    public List<FinalizedEvent> build(String address, ArrayNode args) {
        return Collections.singletonList(new FinalizedEvent(definition, address));
    }
}