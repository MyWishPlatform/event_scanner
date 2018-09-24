package io.mywish.eos.blockchain.model;

import io.mywish.blockchain.ContractEventDefinition;
import lombok.Getter;

@Getter
public class EosActionDefinition extends ContractEventDefinition {
    private final String account;

    public EosActionDefinition(String name, String account) {
        super(name);
        this.account = account;
    }
}
