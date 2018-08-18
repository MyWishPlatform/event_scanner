package io.mywish.eoscli4j.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.Collections;
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
        if (details.transaction == null) {
            return Collections.emptyList();
        }
        return details.transaction.actions;
    }


    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class TransactionDetails {
        private final String id;
        private final EosCompression compression;
        @JsonProperty("packedTrx")
        private final String packedDetails;
        private final TransactionReceipt transaction;

        public TransactionDetails(
                @JsonProperty("id") String id,
                @JsonProperty("compression") EosCompression compression,
                @JsonProperty("packedTrx") String packedDetails,
                @JsonProperty("transaction") TransactionReceipt transaction
        ) {
            this.id = id;
            this.compression = compression;
            this.packedDetails = packedDetails;
            this.transaction = transaction;
        }

        public TransactionDetails(String id) {
            this.id = id;
            this.compression = EosCompression.None;
            this.packedDetails = null;
            this.transaction = null;
        }

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
