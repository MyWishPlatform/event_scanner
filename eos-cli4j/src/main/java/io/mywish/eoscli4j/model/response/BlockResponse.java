package io.mywish.eoscli4j.model.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.mywish.eoscli4j.model.Transaction;
import lombok.Getter;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class BlockResponse extends Response {
//    @JsonFormat(shape = JsonFormat.Shape.STRING,
//            pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS",
//            without = JsonFormat.Feature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE)
    private LocalDateTime timestamp;
    @JsonProperty("block_num")
    private Long blockNum;
    private String id;
    private List<Transaction> transactions;
}
