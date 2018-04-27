package com.glowstick.neocli4j;

import lombok.Getter;

import java.util.List;

public class Block {
    @Getter
    private String hash;

    public static Block parse(String rawBlock) {
        return null;
    }

    public List<Transaction> getTransactions() {
        return null;
    }
}
