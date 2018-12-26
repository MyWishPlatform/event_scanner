package io.mywish.troncli4j.model.contracttype;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class AssetIssueContract extends ContractType {
    private final String name;
    private final String abbr;
    @JsonProperty("total_supply")
    private final Long totalSupply;
    @JsonProperty("frozen_supply")
    private final FrozenSupply frozenSupply;
    @JsonProperty("trx_num")
    private final Integer trxNum;
    private final Integer num;
    @JsonProperty("start_time")
    private final Long startTime;
    @JsonProperty("end_time")
    private final Long endTime;
    private final Long order;
    @JsonProperty("vote_score")
    private final Integer voteScore;
    private final String description;
    private final String url;
    @JsonProperty("free_asset_net_limit")
    private final Long freeAssetNetLimit;
    @JsonProperty("public_free_asset_net_limit")
    private final Long publicFreeAssetNetLimit;
    @JsonProperty("public_free_asset_net_usage")
    private final Long publicFreeAssetNetUsage;
    @JsonProperty("public_latest_free_net_time")
    private final Long publicLatestFreeNetTime;

    @Getter
    @RequiredArgsConstructor
    public static class FrozenSupply {
        @JsonProperty("frozen_amount")
        private final Long frozenAmount;
        @JsonProperty("frozen_days")
        private final Long frozenDays;
    }
}
