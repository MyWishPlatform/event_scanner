package io.mywish.troncli4j.model.contracttype;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class UpdateAssetContract extends ContractType {
    private final String description;
    private final String url;
    @JsonProperty("new_limit")
    private final Long newLimit;
    @JsonProperty("new_public_limit")
    private final Long newPublicLimit;
}
