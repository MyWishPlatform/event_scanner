package io.mywish.troncli4j.model.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.math.BigInteger;

@Getter
@RequiredArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class AccountResponse {
    @JsonProperty("account_name")
    private final String accountName;
    private final String address;
    private final BigInteger balance;
}
