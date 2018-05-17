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

import java.io.UncheckedIOException;
import java.math.BigInteger;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class NeoClientImpl implements NeoClient {
    private final HttpClient httpClient;
    private final HttpPost httpPost;

    public NeoClientImpl(HttpClient httpClient, URI rpc) {
        this.httpClient = httpClient;
        this.httpPost = new HttpPost(rpc);
        httpPost.setHeader("Accept", "application/json");
        httpPost.setHeader("Content-type", "application/json");
    }

    private JsonNode RPC(final String method, final String... params) throws java.io.IOException {
        ObjectNode body = new ObjectMapper().createObjectNode()
                .put("jsonrpc", "2.0")
                .put("id", "mywishscanner")
                .put("method", method);
        ArrayNode paramsField = body.putArray("params");
        Arrays.stream(params).forEach(paramsField::add);
        httpPost.setEntity(new StringEntity(body.toString()));
        return new ObjectMapper().readTree(EntityUtils.toString(this.httpClient.execute(httpPost).getEntity(), "UTF-8")).get("result");
    }

    private JsonNode RPC(final String method) throws java.io.IOException {
        return RPC(method, new String[]{});
    }

    @Override
    public Integer getBlockCount() throws java.io.IOException{
        return RPC("getblockcount").asInt();
    }

    @Override
    public String getBlockHash(Integer blockNumber) throws java.io.IOException {
        return RPC("getblockhash", blockNumber.toString()).asText();
    }

    @Override
    public Block getBlock(String blockHash) throws java.io.IOException {
        Block block = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false).treeToValue(RPC("getblock", blockHash, "1"), Block.class);
        block.getTransactions().forEach(this::initTransaction);
        return block;
    }

    @Override
    public Transaction getTransaction(String txHash, boolean getInputs) throws java.io.IOException {
        Transaction tx = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false).treeToValue(RPC("getrawtransaction", txHash, "1"), Transaction.class);
        if (getInputs) initTransaction(tx);
        return tx;
    }

    @Override
    public List<Event> getEvents(String txHash) throws java.io.IOException {
        List<Event> res = new ArrayList<>();
        JsonNode notifications = RPC("getapplicationlog", txHash);
        if (notifications != null) {
            notifications = notifications.get("notifications");
            if (notifications != null && notifications.isArray()) {
                for (JsonNode notification : notifications) {
                    res.add(new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false).treeToValue(notification, Event.class));
                }
            }
        }
        return res;
    }

    @Override
    public BigInteger getBalance(String address) throws java.io.IOException {
        JsonNode node = RPC("getaccountstate", address);
        if (node == null) return null;
        node = node.get("balances");
        for (JsonNode subNode : node) {
            if ("0xc56f33fc6ecfcd0c225c4ab356fee59390af8560be0e930faebe74a6daff7c9b".equals(subNode.get("asset").asText())) {
                return BigInteger.valueOf(subNode.get("value").asLong());
            }
        }
        return BigInteger.ZERO;
    }

    private void initTransaction(Transaction transaction) {
        transaction.getInputs().forEach(input -> {
            try {
        input.setAddress(getTransaction(input.getTxid(), false).getOutputs().get(0).getAddress());
            } catch (java.io.IOException e) {
                throw new UncheckedIOException(e);
            }
        });
    }
}
