package io.mywish.neocli4j.model;

import lombok.Getter;

import java.util.List;

@Getter
public class JsonRpcRequest {
    private final String jsonrpc;
    private final String id;
    private final String method;
    private final List<Object> params;

    public JsonRpcRequest(String jsonrpc, String id, String method, List<Object> params) {
        this.jsonrpc = jsonrpc;
        this.id = id;
        this.method = method;
        this.params = params;
    }
}
