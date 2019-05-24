package io.mywish.waves.blockchain.services;

import com.fasterxml.jackson.databind.JsonNode;
import io.mywish.blockchain.WrapperOutput;
import io.mywish.blockchain.service.WrapperTransactionService;
import io.mywish.waves.blockchain.model.WrapperTransactionWaves;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class WrapperTransactionWavesService implements WrapperTransactionService<JsonNode> {
    @Autowired
    private WrapperOutputWavesService outputBuilder;

    @Override
    public WrapperTransactionWaves build(JsonNode transaction) {
        List<String> inputs = Collections.singletonList(transaction.get("sender").asText());
        List<WrapperOutput> outputs = Collections.singletonList(outputBuilder.build(transaction));

        boolean contractCreation = transaction.get("type").asInt() == 13;
        String hash = transaction.get("id").asText();

        WrapperTransactionWaves res = new WrapperTransactionWaves(
                hash,
                inputs,
                outputs,
                contractCreation
        );

        if (contractCreation) {
            res.setCreates(inputs.get(0));
        }

        return res;
    }
}
