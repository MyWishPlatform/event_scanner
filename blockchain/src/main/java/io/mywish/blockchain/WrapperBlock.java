package io.mywish.blockchain;

import lombok.Getter;

import java.time.Instant;
import java.util.List;

@Getter
public class WrapperBlock {
    private final String hash;
    private final Long number;
    private final Instant timestamp;
    private final List<WrapperTransaction> transactions;

    public WrapperBlock(String hash, Long number, Instant timestamp, List<WrapperTransaction> transactions) {
        this.hash = hash;
        this.number = number;
        this.timestamp = timestamp;
        this.transactions = transactions;
    }
}
