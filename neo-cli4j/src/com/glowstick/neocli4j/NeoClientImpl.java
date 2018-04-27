package com.glowstick.neocli4j;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;

import java.net.URI;
import java.util.Arrays;
import java.util.stream.Collectors;

public class NeoClientImpl implements NeoClient {
    private final HttpClient httpClient;
    private final URI rpc;
    private final JsonFactory jsonFactory;

    public NeoClientImpl(HttpClient httpClient, URI rpc) {
        this.jsonFactory = new JsonFactory();
        this.httpClient = httpClient;
        this.rpc = rpc;
    }

    private String RPC(String method, Argument... params) throws java.io.IOException {
        HttpPost httpPost = new HttpPost(rpc);
        httpPost.setEntity(new StringEntity("{\"jsonrpc\":\"2.0\",\"id\":\"0\",\"method\":\"" + method + "\",\"params\":[" + String.join(",", Arrays.stream(params).map(Argument::getJSON).collect(Collectors.toList())) + "]}", "UTF-8"));
        httpPost.setHeader("Accept", "application/json");
        httpPost.setHeader("Content-type", "application/json");
        return EntityUtils.toString(this.httpClient.execute(httpPost).getEntity(), "UTF-8");
    }

    private String RPC(String method) throws java.io.IOException {
        return RPC(method, new Argument[]{});
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
        String JSON = RPC("getblockhash", new Argument(blockNumber));
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
        return Block.parse(jsonFactory.createParser(RPC("getblock", new Argument(blockHash), new Argument(1))));
    }

    class Argument {
        private final Integer intValue;
        private final String strValue;

        public Argument(Integer intValue) {
            this.intValue = intValue;
            this.strValue = null;
        }

        public Argument(String strValue) {
            this.strValue = strValue;
            this.intValue = null;
        }

        public String getJSON() {
            if (intValue != null) return String.valueOf(intValue);
            if (strValue != null) return "\"" + strValue + "\"";
            return null;
        }
    }
}
