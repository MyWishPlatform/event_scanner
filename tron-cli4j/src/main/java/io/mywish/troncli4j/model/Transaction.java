package io.mywish.troncli4j.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.mywish.troncli4j.model.contracttype.ContractType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Transaction {
    private final List<TransactionStatusWrapper> ret;
    @Getter
    private final List<String> signature;
    @Getter
    @JsonProperty("txID")
    private final String txId;
    @Getter
    @JsonProperty("raw_data")
    private final RawData rawData;

    public TransactionStatus getStatus() {
        return ret.get(0).getContractRet();
    }

    @Getter
    @RequiredArgsConstructor
    public static class TransactionStatusWrapper {
        private final TransactionStatus contractRet;
    }

    @Getter
    @RequiredArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class RawData {
        private final List<Contract> contract;
        private final Long expiration;
        @JsonProperty("fee_limit")
        private final Long feeLimit;
        private final Long timestamp;
    }

    @Getter
    @RequiredArgsConstructor
    public static class Contract {
        private final Parameter parameter;
        private final ContractType type;
    }

    @Getter
    @RequiredArgsConstructor
    public static class Parameter {
        private final Value value;
        @JsonProperty("type_url")
        private final String typeUrl;
    }

    @Getter
    @RequiredArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Value {
        private String data;
        @JsonProperty("owner_address")
        private String ownerAddress;
        @JsonProperty("contract_address")
        private String contractAddress;
        @JsonProperty("call_value")
        private Long callValue;
    }
}
