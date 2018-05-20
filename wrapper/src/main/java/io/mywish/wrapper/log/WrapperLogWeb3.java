package io.mywish.wrapper.log;

import io.mywish.wrapper.ContractEventDefinition;
import io.mywish.wrapper.WrapperLog;
import io.mywish.wrapper.WrapperType;
import org.web3j.abi.FunctionReturnDecoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Type;
import org.web3j.protocol.core.methods.response.Log;
import java.util.ArrayList;
import java.util.List;

public class WrapperLogWeb3 extends WrapperLog {
    public WrapperLogWeb3(Log log, ContractEventDefinition eventDefinition) {
        super(
                log.getAddress(),
                eventDefinition.getName(),
                new ArrayList<>()
        );
        List<Type> indexedValues = new ArrayList<>();
        List<Type> nonIndexedValues = FunctionReturnDecoder.decode(
                log.getData(),
                eventDefinition.getEvent().getNonIndexedParameters()
        );

        List<TypeReference<Type>> indexedParameters = eventDefinition.getEvent().getIndexedParameters();
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
        for (WrapperType<?> arg : eventDefinition.getTypes()) {
            if (arg.isIndexed()) {
                this.args.add(indexedValues.get(i++).getValue());
            } else {
                this.args.add(nonIndexedValues.get(j++).getValue());
            }
        }
    }
}
