package io.mywish.neocli4j.model;

import lombok.Getter;

@Getter
public class JsonRpcResponse<T> {
    private T result;
    private Error error;
}
