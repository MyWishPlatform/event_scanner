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

    private final static Map<String, String> signatureToNeoNames = new HashMap<String, String>() {{
        put("0xddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3ef", "transfer");
        put("0x0f6798a560793a54c3bcfe86a93cde1e73087d944c0ea20544137d4121396885", "mint");
        put("0xae5184fba832cb2b1f702aca6117b8d265eaf03ad33eb133f19dde0f5920fa08", "finishMint");
        put("0x5daa87a0e9463431830481fd4b6e3403442dfb9a12b9c07597e9f61d50b633c8", "init");
        put("0x8be0079c531659141344cd1fd0a4f28419497f9722a3daafe3b4186f6b6457e0", "transferOwnership");
    }};

    @PostConstruct
    protected void init() throws Exception {
        for (ContractEventBuilder<?> eventBuilder: builders) {
            String signature = eventBuilder.getDefinition().getSignature();
            String neoName = signatureToNeoNames.get(signature);
            if (neoName == null) {
                log.debug("Builder {} ({}) is not supported for Neo.", eventBuilder.getClass().getSimpleName(), signature);
                continue;
            }
            if (buildersByName.containsKey(neoName)) {
                ContractEventBuilder duplicateBuilder = buildersByName.get(neoName);
                if (duplicateBuilder.getClass().equals(eventBuilder.getClass())) {
                    throw new Exception("Duplicate builder " + eventBuilder.getClass() + " with name (" + signature + ") " + neoName + " , skip it.");
                }
                else {
                    log.warn("There two builders with the same name {}: {} and {}. Skip second.", neoName, duplicateBuilder.getClass().getSimpleName(), eventBuilder.getClass().getSimpleName());
                    continue;
                }
            }
            log.info("Added builder {} for NEO event {}.", eventBuilder.getClass().getSimpleName(), neoName);
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
