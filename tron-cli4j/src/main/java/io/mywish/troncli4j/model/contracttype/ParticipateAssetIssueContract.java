package io.mywish.troncli4j.model.contracttype;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ParticipateAssetIssueContract extends ContractType {
    @JsonProperty("to_address")
    private final String toAddress;
    @JsonProperty("asset_name")
    private final String assetName;
    private final Long amount;
}
