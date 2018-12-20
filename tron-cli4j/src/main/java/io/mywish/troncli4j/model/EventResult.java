package io.mywish.troncli4j.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Collections;
import java.util.Map;

@Getter
@RequiredArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class EventResult {
    @JsonProperty("block_number")
    private final Long blockNumber;
    @JsonProperty("block_timestamp")
    private final Long blockTimestamp;
    @JsonProperty("contract_address")
    private final String contractAddress;
    @JsonProperty("event_index")
    private final Long eventIndex;
    @JsonProperty("event_name")
    private final String eventName;
    private final Map<String, String> result;
    @JsonProperty("result_type")
    private final Map<String, String> resultType;
    @JsonProperty("transaction_id")
    private final String transactionId;

    public Map<String, String> getResult() {
        if (result == null) {
            return Collections.emptyMap();
        }
        return result;
    }

    public Map<String, String> getResultType() {
        if (resultType == null) {
            return Collections.emptyMap();
        }
        return resultType;
    }
}
