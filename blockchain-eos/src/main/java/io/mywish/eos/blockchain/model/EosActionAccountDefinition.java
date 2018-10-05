package io.mywish.eos.blockchain.model;

import io.mywish.blockchain.ContractEventDefinition;
import lombok.Getter;

@Getter
public class EosActionAccountDefinition extends ContractEventDefinition {
    private final String account;

    public EosActionAccountDefinition(String name, String account) {
        super(name);
        this.account = account;
    }
}
