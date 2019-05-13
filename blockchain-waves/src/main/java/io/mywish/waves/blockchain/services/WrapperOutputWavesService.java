package io.mywish.waves.blockchain.services;

import com.fasterxml.jackson.databind.JsonNode;
import io.mywish.blockchain.WrapperOutput;
import io.mywish.blockchain.service.WrapperOutputService;
import io.mywish.waves.blockchain.model.WrapperOutputWaves;
import org.springframework.stereotype.Component;

import java.math.BigInteger;

@Component
public class WrapperOutputWavesService implements WrapperOutputService<JsonNode> {
    @Override
    public WrapperOutput build(JsonNode transaction) {
        String address;
        if (transaction.get("type").asInt() == 16) {
            address = transaction.get("dApp").asText();
        } else {
            address = transaction.get("sender").asText();
        }

        return new WrapperOutputWaves(
                transaction.get("id").asText(),
                address,
                BigInteger.ZERO,
                transaction
        );
    }
}
