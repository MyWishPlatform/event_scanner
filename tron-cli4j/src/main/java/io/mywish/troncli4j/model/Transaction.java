package io.mywish.troncli4j.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.mywish.troncli4j.model.contracttype.*;
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
        private final ContractType.Type type;
    }

    @Getter
    @RequiredArgsConstructor
    public static class Parameter {
        @JsonTypeInfo(
                use = JsonTypeInfo.Id.NAME,
                include = JsonTypeInfo.As.EXTERNAL_PROPERTY,
                property = "type_url"
        )
        @JsonSubTypes({
                @JsonSubTypes.Type(value = AccountCreateContract.class, name = "type.googleapis.com/protocol.AccountCreateContract"),
                @JsonSubTypes.Type(value = AccountUpdateContract.class, name = "type.googleapis.com/protocol.AccountUpdateContract"),
                @JsonSubTypes.Type(value = AssetIssueContract.class, name = "type.googleapis.com/protocol.AssetIssueContract"),
                @JsonSubTypes.Type(value = CreateSmartContract.class, name = "type.googleapis.com/protocol.CreateSmartContract"),
                @JsonSubTypes.Type(value = ExchangeCreateContract.class, name = "type.googleapis.com/protocol.ExchangeCreateContract"),
                @JsonSubTypes.Type(value = ExchangeInjectContract.class, name = "type.googleapis.com/protocol.ExchangeInjectContract"),
                @JsonSubTypes.Type(value = ExchangeTransactionContract.class, name = "type.googleapis.com/protocol.ExchangeTransactionContract"),
                @JsonSubTypes.Type(value = ExchangeWithdrawContract.class, name = "type.googleapis.com/protocol.ExchangeWithdrawContract"),
                @JsonSubTypes.Type(value = FreezeBalanceContract.class, name = "type.googleapis.com/protocol.FreezeBalanceContract"),
                @JsonSubTypes.Type(value = ParticipateAssetIssueContract.class, name = "type.googleapis.com/protocol.ParticipateAssetIssueContract"),
                @JsonSubTypes.Type(value = ProposalApproveContract.class, name = "type.googleapis.com/protocol.ProposalApproveContract"),
                @JsonSubTypes.Type(value = ProposalCreateContract.class, name = "type.googleapis.com/protocol.ProposalCreateContract"),
                @JsonSubTypes.Type(value = ProposalDeleteContract.class, name = "type.googleapis.com/protocol.ProposalDeleteContract"),
                @JsonSubTypes.Type(value = SetAccountIdContract.class, name = "type.googleapis.com/protocol.SetAccountIdContract"),
                @JsonSubTypes.Type(value = TransferAssetContract.class, name = "type.googleapis.com/protocol.TransferAssetContract"),
                @JsonSubTypes.Type(value = TransferContract.class, name = "type.googleapis.com/protocol.TransferContract"),
                @JsonSubTypes.Type(value = TriggerSmartContract.class, name = "type.googleapis.com/protocol.TriggerSmartContract"),
                @JsonSubTypes.Type(value = UnfreezeBalanceContract.class, name = "type.googleapis.com/protocol.UnfreezeBalanceContract"),
                @JsonSubTypes.Type(value = UpdateAssetContract.class, name = "type.googleapis.com/protocol.UpdateAssetContract"),
                @JsonSubTypes.Type(value = UpdateSettingContract.class, name = "type.googleapis.com/protocol.UpdateSettingContract"),
                @JsonSubTypes.Type(value = VoteWitnessContract.class, name = "type.googleapis.com/protocol.VoteWitnessContract"),
                @JsonSubTypes.Type(value = WithdrawBalanceContract.class, name = "type.googleapis.com/protocol.WithdrawBalanceContract"),
                @JsonSubTypes.Type(value = WitnessCreateContract.class, name = "type.googleapis.com/protocol.WitnessCreateContract"),
                @JsonSubTypes.Type(value = WitnessUpdateContract.class, name = "type.googleapis.com/protocol.WitnessUpdateContract"),
        })
        private final ContractType value;
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
