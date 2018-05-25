package io.mywish.wrapper.service.log;

import io.mywish.wrapper.ContractEventDefinition;
import io.mywish.wrapper.WrapperLog;
import io.mywish.wrapper.WrapperType;
import org.springframework.stereotype.Component;
import org.web3j.abi.FunctionReturnDecoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Type;
import org.web3j.protocol.core.methods.response.Log;

import java.util.ArrayList;
import java.util.List;

@Component
public class WrapperLogWeb3Service {
    public WrapperLog build(Log log, ContractEventDefinition definition) {
        String address = log.getAddress();
        String name = definition.getName();
        List<Object> args = new ArrayList<>();
        List<Type> indexedValues = new ArrayList<>();
        List<Type> nonIndexedValues = FunctionReturnDecoder.decode(
                log.getData(),
                definition.getEvent().getNonIndexedParameters()
        );

        List<TypeReference<Type>> indexedParameters = definition.getEvent().getIndexedParameters();
        for (int i = 0; i < indexedParameters.size(); i++) {
            indexedValues.add(
                    FunctionReturnDecoder.decodeIndexedValue(
                            log.getTopics().get(i + 1),
                            indexedParameters.get(i)
                    )
            );
        }
        int i = 0;
        int j = 0;
        for (WrapperType<?> arg : definition.getTypes()) {
            if (arg.isIndexed()) {
                args.add(indexedValues.get(i++).getValue());
            } else {
                args.add(nonIndexedValues.get(j++).getValue());
            }
        }
        return new WrapperLog(address, name, args);
    }
}
