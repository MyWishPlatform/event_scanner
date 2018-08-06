package io.mywish.eoscli4j.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@RequiredArgsConstructor
public class Transaction {
    @Getter
    private final TransactionStatus status;
    @JsonProperty("trx")
    private final TransactionDetails details;
    @Getter
    @JsonProperty("cpu_usage_us")
    private final int cpuUsageUs;
    @Getter
    @JsonProperty("net_usage_words")
    private final int netUsageWords;

    public String getId() {
        return details.id;
    }

    public EosCompression getCompression() {
        return details.compression;
    }

    public List<EosAction> getActions() {
        return details.transaction.actions;
    }


    @RequiredArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class TransactionDetails {
        private final String id;
        private final EosCompression compression;
        @JsonProperty("packedTrx")
        private final String packedDetails;
        private final TransactionReceipt transaction;

    }

    @RequiredArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class TransactionReceipt {
        private final LocalDateTime expiration;
        private final long refBlockNum;
        private final long refBlockPrefix;
        private final int maxNetUsageWords;
        private final int maxCpuUsageMs;
        private final List<EosAction> actions;
    }

}
