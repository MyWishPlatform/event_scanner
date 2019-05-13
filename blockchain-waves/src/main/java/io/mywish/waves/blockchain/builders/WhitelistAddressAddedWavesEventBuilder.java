package io.mywish.waves.blockchain.builders;

import com.fasterxml.jackson.databind.node.ArrayNode;
import io.lastwill.eventscan.events.model.contract.crowdsale.WhitelistedAddressAddedEvent;
import io.mywish.blockchain.ContractEventDefinition;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Component
public class WhitelistAddressAddedWavesEventBuilder extends WavesEventBuilder<WhitelistedAddressAddedEvent> {
    private final ContractEventDefinition definition = new ContractEventDefinition("setWhitelistAddresses");

    @Override
    public List<WhitelistedAddressAddedEvent> build(String address, ArrayNode args) {
        String[] addresses = args
                .get(0)
                .get("value")
                .asText()
                .split(",");

        return Arrays
                .stream(addresses)
                .map(addedAddress -> new WhitelistedAddressAddedEvent(
                        definition,
                        address,
                        addedAddress
                ))
                .collect(Collectors.toList());
    }
}
