package io.mywish.troncli4j.model.contracttype;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class UpdateSettingContract {
    @JsonProperty("contract_address")
    private final String contractAddress;
    @JsonProperty("consume_user_resource_percent")
    private final Long consumeUserResourcePercent;
}
