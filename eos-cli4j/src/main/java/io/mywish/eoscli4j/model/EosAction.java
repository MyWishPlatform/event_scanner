package io.mywish.eoscli4j.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class EosAction {
    private final String account;
    private final String name;
//    private final String data;
    @JsonProperty("authorization")
    private final List<ActionAuthorization> authorizations;
    private final JsonNode data;
}
