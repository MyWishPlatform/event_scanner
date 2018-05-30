package io.mywish.neocli4j;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import java.util.List;

@Getter
public class Block {
    private String hash;
    @JsonProperty("time")
    private Long timestamp;
    @JsonProperty("tx")
    private List<Transaction> transactions;
    @JsonProperty("index")
    private Long number;
}
