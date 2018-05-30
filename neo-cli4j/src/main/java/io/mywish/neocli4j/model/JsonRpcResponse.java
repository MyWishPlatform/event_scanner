package io.mywish.neocli4j.model;

import lombok.Getter;

public class JsonRpcResponse<T> {
    @Getter
    private T result;
}
