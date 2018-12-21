package io.mywish.tron.blockchain.model;

import io.mywish.blockchain.WrapperOutput;
import io.mywish.troncli4j.model.contracttype.ContractType;
import lombok.Getter;

import java.math.BigInteger;

@Getter
public class WrapperOutputTron extends WrapperOutput {
    private final ContractType contract;

    public WrapperOutputTron(String parentTransaction, ContractType contract) {
        super(parentTransaction, 0, contract.getOwnerAddress(), BigInteger.ZERO, new byte[0]);
        this.contract = contract;
    }
}
