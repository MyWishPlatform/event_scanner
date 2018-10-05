package io.mywish.blockchain;

import lombok.Getter;

@Getter
public class ContractEventDefinition {
    private final String name;

    public ContractEventDefinition(String name) {
        this.name = name;
    }
}
