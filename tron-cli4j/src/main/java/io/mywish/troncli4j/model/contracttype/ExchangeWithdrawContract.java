package io.mywish.troncli4j.model.contracttype;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ExchangeWithdrawContract extends ContractType {
    @JsonProperty("exchange_id")
    private final Long exchangeId;
    @JsonProperty("token_id")
    private final String tokenId;
    private final Long quant;
}
