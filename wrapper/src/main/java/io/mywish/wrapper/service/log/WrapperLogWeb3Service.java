package io.mywish.wrapper.service.log;

import io.mywish.wrapper.*;
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

@Component
public class WrapperLogWeb3Service {
    @Autowired
    private List<ContractEventBuilder<?>> builders = new ArrayList<>();

    private final Map<String, ContractEventBuilder<?>> buildersBySignature = new HashMap<>();

    @PostConstruct
    protected void init() throws Exception {
        for (ContractEventBuilder<?> eventBuilder : builders) {
            String signature = eventBuilder.getDefinition().getSignature();
            if (buildersBySignature.containsKey(signature)) {
                throw new Exception("Duplicate builder " + eventBuilder.getClass() + " with signature " + signature);
            }
            buildersBySignature.put(signature, eventBuilder);
        }
    }

    public ContractEvent build(Log log) {
        String address = log.getAddress();
        String signature = log.getTopics().get(0);

        ContractEventBuilder<?> builder = buildersBySignature.get(signature);
        ContractEventDefinition eventDefinition = builder.getDefinition();

        List<Object> args = new ArrayList<>();

        List<TypeReference<Type>> indexedParameters = eventDefinition.getEvent().getIndexedParameters();
        for (int i = 0; i < indexedParameters.size(); i++) {
            Type type = FunctionReturnDecoder.decodeIndexedValue(
                    log.getTopics().get(i + 1),
                    indexedParameters.get(i)
            );
            args.add(type.getValue());
        }

        FunctionReturnDecoder.decode(
                log.getData(),
                eventDefinition.getEvent().getNonIndexedParameters()
        )
                .forEach(type -> args.add(type.getValue()));

        return builder.build(address, args);
    }
}
