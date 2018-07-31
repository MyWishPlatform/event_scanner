package io.mywish.eoscli4j.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.mywish.eoscli4j.model.Transaction;
import lombok.Getter;

import java.util.List;

@Getter
public class BlockResponse extends Response {
    @JsonProperty("block_num")
    private Long blockNum;
    private String id;
    private List<Transaction> transactions;
}
