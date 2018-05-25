package io.mywish.wrapper;

import lombok.Getter;
import java.util.List;

@Getter
public class WrapperBlock {
    private final String hash;
    private final Long number;
    private final Long timestamp;
    private final List<WrapperTransaction> transactions;

    public WrapperBlock(String hash, Long number, Long timestamp, List<WrapperTransaction> transactions) {
        this.hash = hash;
        this.number = number;
        this.timestamp = timestamp;
        this.transactions = transactions;
    }
}
