package com.glowstick.neocli4j;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import java.util.List;

@Getter
public class Block {
    private String hash;
    @JsonProperty("time")
    private Long timeSeconds;
    @JsonProperty("tx")
    private List<Transaction> transactions;
}
