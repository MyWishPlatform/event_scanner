package io.mywish.troncli4j.model.contracttype;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class TransferAssetContract extends ContractType {
    @JsonProperty("asset_name")
    private final String assetName;
    @JsonProperty("to_address")
    private final String toAddress;
    private final Long amount;
}
