package io.mywish.troncli4j.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.mywish.troncli4j.TronClient;
import io.mywish.troncli4j.model.EventResult;
import io.mywish.troncli4j.model.request.AccountRequest;
import io.mywish.troncli4j.model.request.BlockByIdRequest;
import io.mywish.troncli4j.model.request.BlockByNumRequest;
import io.mywish.troncli4j.model.request.Request;
import io.mywish.troncli4j.model.response.*;
import io.mywish.troncli4j.model.response.Error;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;

import java.net.URI;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;

@Slf4j
public class TronClientImpl implements TronClient {
    private final HttpClient client;
    private final URI rpc;
    private final ObjectMapper objectMapper;
    private Charset UTF8;

    public TronClientImpl(HttpClient client, URI rpc, ObjectMapper objectMapper) throws Exception {
        this.client = client;
        this.rpc = rpc;
        this.objectMapper = objectMapper;
        this.UTF8 = Charset.forName("UTF-8");
    }

    private <T extends Request> JsonNode doRequest(final String endpoint, final T request) throws Exception {
        HttpPost httpPost = new HttpPost(rpc + endpoint);
        String json = request == null ? "" : objectMapper.writeValueAsString(request);
        httpPost.setEntity(new StringEntity(json));
        HttpResponse httpResponse = this.client.execute(httpPost);
        HttpEntity entity = httpResponse.getEntity();
        String responseBody = EntityUtils.toString(entity, UTF8);
        JsonNode node = objectMapper.readTree(responseBody);
        if (node.get("error") != null) {
            Error error = objectMapper.treeToValue(node, Response.class).getError();
            throw new Exception(error.toString());
        }
        return node;
    }

    private JsonNode doRequest(final String endpoint) throws Exception {
        HttpGet httpGet = new HttpGet(rpc + endpoint);
        HttpResponse httpResponse = this.client.execute(httpGet);
        HttpEntity entity = httpResponse.getEntity();
        String responseBody = EntityUtils.toString(entity, UTF8);
        JsonNode node = objectMapper.readTree(responseBody);
        if (node.get("error") != null) {
            Error error = objectMapper.treeToValue(node, Response.class).getError();
            throw new Exception(error.toString());
        }
        return node;
    }

    @Override
    public NodeInfoResponse getNodeInfo() throws Exception {
        return objectMapper.treeToValue(
                doRequest("/wallet/getnodeinfo", null),
                NodeInfoResponse.class
        );
    }

    private BlockResponse parseBlock(JsonNode blockNode) throws Exception {
        return objectMapper.treeToValue(blockNode, BlockResponse.class);
    }

    @Override
    public BlockResponse getBlock(String id) throws Exception {
        return parseBlock(doRequest("/wallet/getblockbyid", new BlockByIdRequest(id)));
    }

    @Override
    public BlockResponse getBlock(Long number) throws Exception {
        return parseBlock(doRequest("/wallet/getblockbynum", new BlockByNumRequest(number)));
    }

//    @Override
//    public List<EventResult> getEventResult(String base58ContractAddress, String event, Long blockNum) throws Exception {
//        String url = String.join("/", "/event" + base58ContractAddress, event, blockNum.toString());
//        return Arrays.asList(objectMapper.treeToValue(doRequest(url), EventResult[].class));
//    }

    @Override
    public List<EventResult> getEventResult(String txId) throws Exception {
        return Arrays.asList(objectMapper.treeToValue(
                doRequest("/event/transaction/" + txId), EventResult[].class));
    }

    @Override
    public AccountResponse getAccount(String hexAddress) throws Exception {
        return objectMapper.treeToValue(
                doRequest("/wallet/getaccount", new AccountRequest(hexAddress)),
                AccountResponse.class);
    }
}
