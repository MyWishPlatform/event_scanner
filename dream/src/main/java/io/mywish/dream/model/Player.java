package io.mywish.dream.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.math.BigInteger;

@Getter
@RequiredArgsConstructor
public class Player {
    private final int index;
    private final String address;
    private final BigInteger ticketAmount;
    private final BigInteger dreamAmount;
}
