package io.mywish.troncli4j.model.contracttype;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

public abstract class ContractType {
    @Getter
    @Setter
    @JsonProperty("owner_address")
    private String ownerAddress;
}
