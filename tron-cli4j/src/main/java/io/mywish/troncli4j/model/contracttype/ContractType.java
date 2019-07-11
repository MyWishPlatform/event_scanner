package io.mywish.troncli4j.model.contracttype;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

public abstract class ContractType {
    @Getter
    @Setter
    @JsonProperty("owner_address")
    private String ownerAddress;

    public enum Type {
        AccountCreateContract,
        TransferContract,
        TransferAssetContract,
        VoteAssetContract,
        VoteWitnessContract,
        WitnessCreateContract,
        AssetIssueContract,
        WitnessUpdateContract,
        ParticipateAssetIssueContract,
        AccountUpdateContract,
        FreezeBalanceContract,
        UnfreezeBalanceContract,
        WithdrawBalanceContract,
        UnfreezeAssetContract,
        UpdateAssetContract,
        ProposalCreateContract,
        ProposalApproveContract,
        ProposalDeleteContract,
        SetAccountIdContract,
        CustomContract,
        CreateSmartContract,
        TriggerSmartContract,
        GetContract,
        UpdateSettingContract,
        ExchangeCreateContract,
        ExchangeInjectContract,
        ExchangeWithdrawContract,
        ExchangeTransactionContract,
        UpdateEnergyLimitContract,
        AccountPermissionUpdateContract,
        ClearABIContract
    }
}
