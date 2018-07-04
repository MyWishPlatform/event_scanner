package io.lastwill.eventscan.model;

import lombok.Getter;

import java.math.BigInteger;

@Getter
public class AirdropEntry {
    private final String address;
    private final BigInteger value;

    public AirdropEntry(String address, BigInteger value) {
        this.address = address;
        this.value = value;
    }
}
