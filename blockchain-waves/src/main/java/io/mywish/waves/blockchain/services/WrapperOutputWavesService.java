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
        return new WrapperOutputWaves(
                transaction.get("id").asText(),
                transaction.get("sender").asText(),
                BigInteger.ZERO,
                transaction
        );
    }
}
