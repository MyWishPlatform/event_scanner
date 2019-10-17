package io.mywish.web3.blockchain.model;

import lombok.Getter;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Type;

@Getter
public class WrapperType<T extends Type> {
    private TypeReference<T> typeReference;

    private WrapperType(TypeReference<T> typeReference) {
        this.typeReference = typeReference;
    }

    public static <T extends Type> WrapperType<T> create(final Class<T> c, boolean indexed) {
        return new WrapperType<>(TypeReference.create(c, indexed));
    }
}
