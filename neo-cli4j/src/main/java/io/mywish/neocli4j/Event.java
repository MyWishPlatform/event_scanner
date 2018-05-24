package io.mywish.neocli4j;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.Getter;

import javax.xml.bind.DatatypeConverter;
import java.util.ArrayList;
import java.util.List;

@Getter
public class Event {
    private String contract;
    private String name;
    private List<String> arguments = new ArrayList<>();

    @JsonProperty("state")
    private void init(JsonNode state) {
        JsonNode value = state.get("value");
        if (value.isArray()) {
            for (JsonNode event : value) {
                String text = new String(DatatypeConverter.parseHexBinary(event.get("value").asText()));
                if (name == null) name = text;
                else arguments.add(text);
            }
        } else {
            // TODO
        }
    }
}
