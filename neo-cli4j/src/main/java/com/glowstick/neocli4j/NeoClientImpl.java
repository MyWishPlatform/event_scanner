package com.glowstick.neocli4j;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
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
    private final URI rpc;
    private final JsonFactory jsonFactory;
    private final BlockParser blockParser;

    public NeoClientImpl(HttpClient httpClient, URI rpc) {
        this.jsonFactory = new JsonFactory();
        this.httpClient = httpClient;
        this.rpc = rpc;
        this.blockParser = new BlockParser();
    }

    private String RPC(String method, String... params) throws java.io.IOException {
        ObjectNode body = new ObjectMapper().createObjectNode();
        body.put("jsonrpc", "2.0");
        body.put("id", "mywishscanner");
        body.put("method", method);
        ArrayNode paramsField = body.putArray("params");
        Arrays.stream(params).forEach(paramsField::add);
        HttpPost httpPost = new HttpPost(rpc);
        httpPost.setEntity(new StringEntity(body.toString()));
        httpPost.setHeader("Accept", "application/json");
        httpPost.setHeader("Content-type", "application/json");
        return EntityUtils.toString(this.httpClient.execute(httpPost).getEntity(), "UTF-8");
    }

    private String RPC(String method) throws java.io.IOException {
        return RPC(method, new String[]{});
    }

    public Integer getBlockCount() throws java.io.IOException{
        String JSON = RPC("getblockcount");
        JsonParser parser = jsonFactory.createParser(JSON);
        while (parser.nextToken() != JsonToken.END_OBJECT) {
            if ("result".equals(parser.getCurrentName())) {
                parser.nextToken();
                return Integer.valueOf(parser.getText());
            }
        }
        return null;
    }

    public String getBlockHash(Integer blockNumber) throws java.io.IOException {
        String JSON = RPC("getblockhash", blockNumber.toString());
        JsonParser parser = jsonFactory.createParser(JSON);
        while (parser.nextToken() != JsonToken.END_OBJECT) {
            if ("result".equals(parser.getCurrentName())) {
                parser.nextToken();
                return parser.getText();
            }
        }
        return null;
    }

    public Block getBlock(String blockHash) throws java.io.IOException {
        return blockParser.parse(RPC("getblock", blockHash, "1"));
    }
}
