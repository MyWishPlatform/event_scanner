package io.mywish.troncli4j.model.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.mywish.troncli4j.model.Transaction;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Collections;
import java.util.List;

@RequiredArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class BlockResponse extends Response {
    @Getter
    @JsonProperty("blockID")
    private final String blockId;
    @JsonProperty("block_header")
    private final BlockHeader blockHeader;
    private final List<Transaction> transactions;

    public RawData getBlockHeader() {
        return blockHeader.getRawData();
    }

    public List<Transaction> getTransactions() {
        if (transactions == null) {
            return Collections.emptyList();
        }
        return transactions;
    }

    @Getter
    @RequiredArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class BlockHeader {
        @JsonProperty("raw_data")
        private final RawData rawData;
    }

    @Getter
    @RequiredArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class RawData {
        private final Long number;
        private final Long timestamp;
    }
}
