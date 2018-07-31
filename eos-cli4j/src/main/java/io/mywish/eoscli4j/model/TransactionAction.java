package io.mywish.eoscli4j.model;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Getter;

@Getter
public class TransactionAction {
    private String account;
    private String name;
    private JsonNode data;
}
