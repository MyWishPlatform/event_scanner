package io.mywish.dream.dto;

import lombok.Getter;

@Getter
public class CreateContract {
    private final String address;

    public CreateContract(String address) {
        this.address = address;
    }
}
