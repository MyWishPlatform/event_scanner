package io.mywish.troncli4j.model.contracttype;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class TransferContract extends ContractType {
    @JsonProperty("to_address")
    private final String toAddress;
    private final Long amount;
}
