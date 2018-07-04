package io.mywish.wrapper;

import lombok.Getter;
import org.spongycastle.util.Arrays;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Bool;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.Uint;

import javax.xml.bind.DatatypeConverter;
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

    public static List<Object> argsFromBytes(List<byte[]> args, List<WrapperType<?>> types) {
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
