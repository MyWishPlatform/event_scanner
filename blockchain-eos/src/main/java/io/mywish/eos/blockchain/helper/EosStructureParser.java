package io.mywish.eos.blockchain.helper;

import io.mywish.eos.blockchain.model.MaxSupply;
import io.mywish.eos.blockchain.model.Symbol;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class EosStructureParser {
    public MaxSupply parseMaxSupply(String maxSupply) {
        String[] parts = maxSupply.split(" ");
        if (parts.length != 2) {
            throw new IllegalArgumentException("maxSupply must be in format 'XXXX.XXXX SYMB'");
        }

        BigDecimal value = new BigDecimal(parts[0]);
        String symbol = parts[1];

        return new MaxSupply(value, symbol, value.scale());
    }

    public Symbol parseSymbol(String symbol) {
        String[] parts = symbol.split(",");
        if (parts.length != 2) {
            throw new IllegalArgumentException("symbol must be in format 'X,SYMB'");
        }

        int decimals = Integer.parseInt(parts[0]);
        String strSymbol = parts[1];

        return new Symbol(strSymbol, decimals);
    }
}
