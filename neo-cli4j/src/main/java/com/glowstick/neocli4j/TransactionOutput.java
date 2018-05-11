package com.glowstick.neocli4j;

import lombok.Getter;

@Getter
public class TransactionOutput {
    private String address;
    private String asset;
    private Double value;
}
