package io.mywish.troncli4j.model.contracttype;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class SetAccountIdContract extends ContractType {
    @JsonProperty("account_id")
    private final String accountId;
}
