package io.mywish.scanner;

import lombok.Getter;
import java.util.List;

@Getter
public class WrapperBlock {
    private final Long number;
    private final Long timestamp;
    private final List<WrapperTransaction> transactions;

    protected WrapperBlock(Long number, Long timestamp, List<WrapperTransaction> transactions) {
        this.number = number;
        this.timestamp = timestamp;
        this.transactions = transactions;
    }
}
