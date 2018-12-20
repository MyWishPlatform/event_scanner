package io.mywish.troncli4j.model.contracttype;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ExchangeCreateContract extends ContractType {
    @JsonProperty("first_token_id")
    private final String firstTokenId;
    @JsonProperty("first_token_balance")
    private final Long firstTokenBalance;
    @JsonProperty("second_token_id")
    private final String secondTokenId;
    @JsonProperty("second_token_balance")
    private final Long secondTokenBalance;
}
