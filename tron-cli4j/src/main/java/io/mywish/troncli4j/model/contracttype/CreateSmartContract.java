package io.mywish.troncli4j.model.contracttype;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.mywish.troncli4j.model.SmartContract;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class CreateSmartContract extends ContractType {
    @JsonProperty("new_contract")
    private final SmartContract newContract;
}
