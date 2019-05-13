package io.mywish.wavescli4j.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.Getter;

import java.math.BigInteger;
import java.util.List;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class Block implements Response {
    private Long height;
    private Long timestamp;
    private String signature;
    private BigInteger fee;
    private List<JsonNode> transactions;
}
