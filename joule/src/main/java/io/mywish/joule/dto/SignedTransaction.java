package io.mywish.joule.dto;

import lombok.Getter;

@Getter
public class SignedTransaction {
    private final String result;

    public SignedTransaction(String result) {
        this.result = result;
    }
}
