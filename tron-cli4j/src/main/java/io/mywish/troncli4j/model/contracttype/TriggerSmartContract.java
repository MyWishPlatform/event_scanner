package io.mywish.troncli4j.model.contracttype;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class TriggerSmartContract extends ContractType {
    @JsonProperty("contract_address")
    private final String contractAddress;
    @JsonProperty("call_value")
    private final Long callValue;
    private final String data;
}
