package io.mywish.wrapper.service.log;

import io.mywish.neocli4j.Event;
import io.mywish.wrapper.*;
import lombok.extern.slf4j.Slf4j;
import org.spongycastle.util.Arrays;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Bool;
import org.web3j.abi.datatypes.Uint;

import javax.annotation.PostConstruct;
import javax.xml.bind.DatatypeConverter;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class WrapperLogNeoService {
    @Autowired
    private List<ContractEventBuilder<?>> builders = new ArrayList<>();
    private Map<String, ContractEventBuilder<?>> buildersByName = new HashMap<>();

    private final static Map<String, String> ethToNeoNames = new HashMap<String, String>() {{
        put("transfer", "Transfer");
        put("mint", "Mint");
        put("finishMint", "MintFinished");
        put("init", "Initialized");
        put("transferOwnership", "OwnershipTransferred");
    }};

    @PostConstruct
    protected void init() throws Exception {
        for (ContractEventBuilder<?> eventBuilder: builders) {
            String name = eventBuilder.getDefinition().getName();
            String neoName = ethToNeoNames.get(name);
            if (neoName == null) {
                log.debug("Builder {} is not supported for Neo.", name);
                continue;
            }
            if (buildersByName.containsKey(neoName)) {
                throw new Exception("Duplicate builder " + eventBuilder.getClass() + " with name (" + name + ") " + neoName + " , skip it.");
            }
            buildersByName.put(neoName, eventBuilder);
        }
    }

    public ContractEvent build(Event event) {
        ContractEventBuilder builder = buildersByName.get(event.getName());
        if (builder == null) {
            log.warn("There is not builder for NEO event {}.", event.getName());
            return null;
        }
        List<Object> args = argsFromBytes(event.getArguments(), builder.getDefinition().getTypes());

        return builder.build(event.getContract(), args);
    }

    private static List<Object> argsFromBytes(List<byte[]> args, List<WrapperType<?>> types) {
        List<Object> res = new ArrayList<>();
        for (int i = 0; i < args.size(); i++) {
            WrapperType<?> type = types.get(i);
            byte[] arg = args.get(i);
            if (type.getTypeReference().getType() == Address.class) {
                res.add("0x" + DatatypeConverter.printHexBinary(arg).toLowerCase());
            }
            if (type.getTypeReference().getType() == Uint.class) {
                res.add(new BigInteger(Arrays.reverse(arg)));
            }
            if (type.getTypeReference().getType() == Bool.class) {
                res.add(arg[0] != 0);
            }
        }
        return res;
    }

}
