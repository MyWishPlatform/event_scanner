package io.mywish.eoscli4j.model.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.math.BigInteger;

@RequiredArgsConstructor
@Getter
public class BalanceResponse extends Response {
    private final BigInteger value;
    private final int decimals;
}
