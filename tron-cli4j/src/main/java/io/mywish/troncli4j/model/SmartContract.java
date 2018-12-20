package io.mywish.troncli4j.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class SmartContract {
    @JsonProperty("origin_address")
    private final String originAddress;
    @JsonProperty("contract_address")
    private final String contractAddress;
    @JsonProperty("call_value")
    private final Long callValue;
    @JsonProperty("consume_user_resource_percent")
    private final Long consumeUserResourcePercent;
    private final String name;
    @JsonProperty("origin_energy_limit")
    private final Long origin_energy_limit;
}
