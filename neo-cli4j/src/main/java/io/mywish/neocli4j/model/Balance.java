package io.mywish.neocli4j.model;

import lombok.Getter;

import java.math.BigInteger;

@Getter
public class Balance {
    private String asset;
    private BigInteger value;
}
