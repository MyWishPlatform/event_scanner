package com.glowstick.neocli4j;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import java.net.URI;
import java.util.Arrays;

public class NeoClientImpl implements NeoClient {
    private final HttpClient httpClient;
    private final HttpPost httpPost;

    public NeoClientImpl(HttpClient httpClient, URI rpc) {
        this.httpClient = httpClient;
        this.httpPost = new HttpPost(rpc);
        httpPost.setHeader("Accept", "application/json");
        httpPost.setHeader("Content-type", "application/json");
    }

    private JsonNode RPC(String method, String... params) throws java.io.IOException {
        ObjectNode body = new ObjectMapper().createObjectNode()
                .put("jsonrpc", "2.0")
                .put("id", "mywishscanner")
                .put("method", method);
        ArrayNode paramsField = body.putArray("params");
        Arrays.stream(params).forEach(paramsField::add);
        httpPost.setEntity(new StringEntity(body.toString()));
        return new ObjectMapper().readTree(EntityUtils.toString(this.httpClient.execute(httpPost).getEntity(), "UTF-8")).get("result");
    }

    private JsonNode RPC(String method) throws java.io.IOException {
        return RPC(method, new String[]{});
    }

    public Integer getBlockCount() throws java.io.IOException{
        return RPC("getblockcount").asInt();
    }

    public String getBlockHash(Integer blockNumber) throws java.io.IOException {
        return RPC("getblockhash", blockNumber.toString()).asText();
    }

    public Block getBlock(String blockHash) throws java.io.IOException {
        return new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false).treeToValue(RPC("getblock", blockHash, "1"), Block.class);
    }
}
