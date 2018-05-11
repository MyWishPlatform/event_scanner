package com.glowstick.neocli4j;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
        String json = RPC("getblock", blockHash, "1");
        JsonNode node = new ObjectMapper().readTree(json).get("result");
        String hash = node.get("hash").textValue();
        Long time = node.get("time").longValue();
        List<Transaction> tx = new ArrayList<>();
        if (node.get("tx").isArray()) node.get("tx").forEach(txNode -> {
            Transaction.Type txType = null;
            String txHash = txNode.get("txid").textValue();
            List<TransactionOutput> txOutputs = new ArrayList<>();
            String txScript = null;

            String type = txNode.get("type").textValue();
            if ("MinerTransaction".equals(type)) txType = Transaction.Type.Miner;
            else if ("ClaimTransaction".equals(type)) txType = Transaction.Type.Claim;
            else if ("ContractTransaction".equals(type)) txType = Transaction.Type.Contract;
            else if ("InvocationTransaction".equals(type)) txType = Transaction.Type.Invocation;

            txNode.get("vout").forEach(txOut -> txOutputs.add(new TransactionOutput(
                    txOut.get("address").textValue(),
                    txOut.get("asset").textValue(),
                    txOut.get("value").asDouble()
            )));

            if (txNode.get("script") != null) txScript = txNode.get("script").textValue();

            tx.add(new Transaction(txType, txHash, txOutputs, txScript));
        });
        return new Block(hash, time, tx);
    }
}
