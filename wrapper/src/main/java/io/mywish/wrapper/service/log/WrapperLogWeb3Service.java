package io.mywish.wrapper.service.log;

import io.mywish.wrapper.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.web3j.abi.FunctionReturnDecoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Type;
import org.web3j.protocol.core.methods.response.Log;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class WrapperLogWeb3Service {
    @Autowired
    private List<ContractEventBuilder<?>> builders;

    private final Map<String, ContractEventBuilder<?>> buildersBySignature = new HashMap<>();

    @PostConstruct
    protected void init() throws Exception {
        for (ContractEventBuilder<?> eventBuilder : builders) {
            String signature = eventBuilder.getDefinition().getSignature();
            if (buildersBySignature.containsKey(signature)) {
                throw new Exception("Duplicate builder " + eventBuilder.getClass() + " with signature " + signature);
            }
            log.info("Added builder {} for event with signature {}.", eventBuilder.getClass().getSimpleName(), signature);
            buildersBySignature.put(signature, eventBuilder);
        }
    }

    public ContractEvent build(Log logParam) {
        String address = logParam.getAddress();
        String signature = logParam.getTopics().get(0);

        ContractEventBuilder<?> builder = buildersBySignature.get(signature);
        if (builder == null) {
            log.warn("There is not builder for ETH event with signature {}.", signature);
            return null;
        }
        ContractEventDefinition eventDefinition = builder.getDefinition();

        List<Object> args = new ArrayList<>();

        List<TypeReference<Type>> indexedParameters = eventDefinition.getEvent().getIndexedParameters();
        for (int i = 0; i < indexedParameters.size(); i++) {
            Type type = FunctionReturnDecoder.decodeIndexedValue(
                    logParam.getTopics().get(i + 1),
                    indexedParameters.get(i)
            );
            args.add(type.getValue());
        }

        FunctionReturnDecoder.decode(
                logParam.getData(),
                eventDefinition.getEvent().getNonIndexedParameters()
        )
                .forEach(type -> args.add(type.getValue()));

        return builder.build(address, args);
    }
}
