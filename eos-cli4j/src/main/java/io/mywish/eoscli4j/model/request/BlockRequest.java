package io.mywish.eoscli4j.model.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class BlockRequest extends Request {
    @JsonProperty("block_num_or_id")
    private final String id;

    public BlockRequest(Long number) {
        this.id = Long.toString(number);
    }

    public BlockRequest(String hash) {
        this.id = hash;
    }
}
