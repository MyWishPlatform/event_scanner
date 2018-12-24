package io.mywish.troncli4j.model.contracttype;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class WitnessUpdateContract extends ContractType {
    @JsonProperty("update_url")
    private final String updateUrl;
}
