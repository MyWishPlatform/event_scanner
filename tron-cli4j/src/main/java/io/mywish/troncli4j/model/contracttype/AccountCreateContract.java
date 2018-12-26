package io.mywish.troncli4j.model.contracttype;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class AccountCreateContract extends ContractType {
    @JsonProperty("account_address")
    private final String accountAddress;
    private final AccountType type;

    public enum AccountType {
        Normal,
        AssetIssue,
        Contract,
    }
}
