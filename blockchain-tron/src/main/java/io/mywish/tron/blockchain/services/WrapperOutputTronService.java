package io.mywish.tron.blockchain.services;

import com.fasterxml.jackson.databind.JsonNode;
import io.mywish.blockchain.WrapperOutput;
import io.mywish.blockchain.service.WrapperOutputService;
import io.mywish.tron.blockchain.model.WrapperOutputTron;
import io.mywish.troncli4j.model.Transaction;
import org.springframework.stereotype.Component;

import java.math.BigInteger;

@Component
public class WrapperOutputTronService implements WrapperOutputService<Transaction> {
    private static final String TRIGGER_TYPE = "TriggerSmartContract";
    private static final String TRANSFER_TYPE = "TransferContract";

    @Override
    public WrapperOutput build(Transaction transaction) {
        Transaction.Contract contractWrapper = transaction.getRawData().getContract().get(0);
        String contractType = contractWrapper.getType();
        JsonNode contract = contractWrapper.getParameter().getValue();

        String outputAddress;
        BigInteger value = BigInteger.ZERO;
        if (contractType.equals(TRIGGER_TYPE)) {
            outputAddress = contract.get("contract_address").asText();
            if (contract.has("call_value")) {
                value = contract.get("call_value").bigIntegerValue();
            }
        } else if (contractType.equals(TRANSFER_TYPE)) {
            outputAddress = contract.get("to_address").asText();
            value = contract.get("amount").bigIntegerValue();
        } else {
            outputAddress = contract.get("owner_address").asText();
        }

        return new WrapperOutputTron(
                transaction.getTxId(),
                outputAddress,
                value,
                contract
        );
    }
}
