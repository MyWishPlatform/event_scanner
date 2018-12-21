package io.mywish.troncli4j.model.contracttype;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class FreezeBalanceContract extends ContractType {
    @JsonProperty("frozen_balance")
    private Long frozenBalance;
    @JsonProperty("frozen_duration")
    private Long frozenDuration;
    private ResourceCode resource;
}
