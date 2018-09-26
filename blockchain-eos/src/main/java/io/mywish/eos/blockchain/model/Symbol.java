package io.mywish.eos.blockchain.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class Symbol {
    private final String symbol;
    private final int decimals;
}
