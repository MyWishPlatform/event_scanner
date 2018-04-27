package com.glowstick.neocli4j;

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

    public NeoClientImpl(HttpClient httpClient, URI rpc) {
        this.httpClient = httpClient;
        this.rpc = rpc;
    }

    private String RPC(String method, Argument... params) {
        try {
            HttpPost httpPost = new HttpPost(rpc);
            httpPost.setEntity(new StringEntity("{\"jsonrpc\":\"2.0\",\"id\":\"0\",\"method\":\"" + method + "\",\"params\":[" + String.join(",", Arrays.stream(params).map(Argument::getJSON).collect(Collectors.toList())) + "]}", "UTF-8"));
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");
            return EntityUtils.toString(this.httpClient.execute(httpPost).getEntity(), "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private String RPC(String method) {
        return RPC(method, new Argument[]{});
    }

    public Integer getBlockCount() {
        return Integer.valueOf(RPC("getblockcount"));
    }

    public String getBlockHash(Integer blockNumber) {
        return RPC("getblockhash", new Argument(blockNumber));
    }

    public Block getBlock(String blockHash) {
        System.out.println(RPC("getblock", new Argument(blockHash), new Argument(1)));
        return null;
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
