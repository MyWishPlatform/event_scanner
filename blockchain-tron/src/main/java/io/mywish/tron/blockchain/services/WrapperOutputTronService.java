package io.mywish.tron.blockchain.services;

import com.fasterxml.jackson.databind.JsonNode;
import io.mywish.blockchain.WrapperOutput;
import io.mywish.blockchain.service.WrapperOutputService;
import io.mywish.tron.blockchain.model.WrapperOutputTron;
import io.mywish.troncli4j.model.Transaction;
import io.mywish.troncli4j.model.contracttype.ContractType;
import org.springframework.stereotype.Component;

import java.math.BigInteger;

@Component
public class WrapperOutputTronService implements WrapperOutputService<Transaction> {
    @Override
    public WrapperOutput build(Transaction transaction) {
        Transaction.Contract contractWrapper = transaction.getRawData().getContract().get(0);
        ContractType.Type contractType = contractWrapper.getType();
        JsonNode contract = contractWrapper.getParameter().getValue();

        String outputAddress;
        BigInteger value;
        if (contractType.equals(ContractType.Type.TriggerSmartContract)) {
            outputAddress = contract.get("contract_address").asText();
            value = contract.get("call_value").bigIntegerValue();
        }
        else if (contractType.equals(ContractType.Type.TransferContract)) {
            outputAddress = contract.get("to_address").asText();
            value = contract.get("amount").bigIntegerValue();
        }
        else {
            outputAddress = contract.get("owner_address").asText();
            value = BigInteger.ZERO;
        }

        return new WrapperOutputTron(
                transaction.getTxId(),
                outputAddress,
                value,
                contract
        );
    }
}
