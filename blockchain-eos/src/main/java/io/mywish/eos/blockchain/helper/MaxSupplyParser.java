package io.mywish.eos.blockchain.helper;

import io.mywish.eos.blockchain.model.MaxSupply;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class MaxSupplyParser {
    public MaxSupply parse(String maxSupply) {
        String[] parts = maxSupply.split(" ");
        if (parts.length != 2) {
            throw new IllegalArgumentException("maxSupply must be in format 'XXXX.XXXX SYMB'");
        }

        BigDecimal value = new BigDecimal(parts[0]);
        String symbol = parts[1];

        return new MaxSupply(value, symbol, value.scale());
    }
}
