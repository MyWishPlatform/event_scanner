package io.mywish.wrapper;

import lombok.Getter;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Bool;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.Uint;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

@Getter
public class WrapperType<T extends Type> {
    private TypeReference<T> typeReference;
    private boolean indexed;

    private WrapperType(TypeReference<T> typeReference, boolean indexed) {
        this.typeReference = typeReference;
        this.indexed = indexed;
    }

    public static <T extends Type> WrapperType<T> create(final Class<T> c, boolean indexed) {
        return new WrapperType<T>(TypeReference.create(c), indexed);
    }

    public static List<Object> argsFromString(List<String> args, List<WrapperType<?>> types) {
        List<Object> res = new ArrayList<>();
        for (int i = 0; i < args.size(); i++) {
            WrapperType<?> type = types.get(i);
            String arg = args.get(i);
            if (type.getTypeReference().getType() == Address.class) {
                res.add(arg);
            }
            if (type.getTypeReference().getType() == Uint.class) {
                res.add(new BigInteger(arg));
            }
            if (type.getTypeReference().getType() == Bool.class) {
                res.add(Boolean.valueOf(arg));
            }
        }
        return res;
    }
}
