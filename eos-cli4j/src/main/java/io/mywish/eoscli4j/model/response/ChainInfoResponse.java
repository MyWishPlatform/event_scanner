package io.mywish.eoscli4j.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class ChainInfoResponse extends Response {
    @JsonProperty("head_block_num")
    private Long headBlockNum;
    @JsonProperty("last_irreversible_block_num")
    private Long lastIrreversibleBlockNum;
}
