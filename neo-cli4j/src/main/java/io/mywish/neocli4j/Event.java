package io.mywish.neocli4j;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.Getter;

import javax.xml.bind.DatatypeConverter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class Event {
    private String name;
    private String contract;
    private List<byte[]> arguments = new ArrayList<>();

    @JsonProperty("args")
    private void parseArgs(List<String> data) {
        data.remove(0);
        this.arguments = data
                .stream()
                .map(DatatypeConverter::parseHexBinary)
                .collect(Collectors.toList());
    }
}
