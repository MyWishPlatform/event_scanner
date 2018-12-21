package io.mywish.tron.blockchain.services;

import io.mywish.blockchain.WrapperOutput;
import io.mywish.blockchain.WrapperTransaction;
import io.mywish.blockchain.service.WrapperTransactionService;
import io.mywish.tron.blockchain.model.WrapperOutputTron;
import io.mywish.tron.blockchain.model.WrapperTransactionTron;
import io.mywish.troncli4j.model.Transaction;
import io.mywish.troncli4j.model.contracttype.ContractType;
import io.mywish.troncli4j.model.contracttype.CreateSmartContract;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class WrapperTransactionTronService implements WrapperTransactionService<Transaction> {
    @Override
    public WrapperTransaction build(Transaction transaction) {
        String hash = transaction.getTxId();
        Transaction.Contract contractWrapper = transaction.getRawData().getContract().get(0);
        ContractType contract = contractWrapper.getParameter().getValue();

        List<String> inputs = Collections.singletonList(contract.getOwnerAddress());
        List<WrapperOutput> outputs = Collections.singletonList(new WrapperOutputTron(hash, contract));

        boolean contractCreation = contractWrapper.getType().equals(ContractType.Type.CreateSmartContract);

        WrapperTransactionTron res = new WrapperTransactionTron(
                hash,
                inputs,
                outputs,
                contractCreation,
                transaction.getStatus()
        );

        if (contractCreation) {
            CreateSmartContract createSmartContract = (CreateSmartContract) contract;
            res.setCreates(createSmartContract.getNewContract().getContractAddress());
        }

        return res;
    }
}
