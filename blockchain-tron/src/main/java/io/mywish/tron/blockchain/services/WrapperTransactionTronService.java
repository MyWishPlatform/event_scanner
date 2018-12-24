package io.mywish.tron.blockchain.services;

import com.fasterxml.jackson.databind.JsonNode;
import io.mywish.blockchain.WrapperOutput;
import io.mywish.blockchain.WrapperTransaction;
import io.mywish.blockchain.service.WrapperTransactionService;
import io.mywish.tron.blockchain.model.WrapperOutputTron;
import io.mywish.tron.blockchain.model.WrapperTransactionTron;
import io.mywish.troncli4j.model.Transaction;
import io.mywish.troncli4j.model.contracttype.ContractType;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class WrapperTransactionTronService implements WrapperTransactionService<Transaction> {
    @Override
    public WrapperTransaction build(Transaction transaction) {
        String hash = transaction.getTxId();
        Transaction.Contract contractWrapper = transaction.getRawData().getContract().get(0);
        JsonNode contract = contractWrapper.getParameter().getValue();

        String ownerAddress = contract.get("owner_address").asText();
        List<String> inputs = Collections.singletonList(ownerAddress);
        List<WrapperOutput> outputs = Collections.singletonList(new WrapperOutputTron(hash, ownerAddress, contract));

        boolean contractCreation = contractWrapper.getType().equals(ContractType.Type.CreateSmartContract);

        List<String> contracts = transaction.getContractAddress() == null
                ? Collections.emptyList()
                : Collections.singletonList(transaction.getContractAddress());

        WrapperTransactionTron res = new WrapperTransactionTron(
                hash,
                inputs,
                outputs,
                contractCreation,
                contracts,
                transaction.getStatus()
        );

        if (contractCreation) {
            res.setCreates(transaction.getContractAddress());
        }

        return res;
    }
}
