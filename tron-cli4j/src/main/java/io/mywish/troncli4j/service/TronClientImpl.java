package io.mywish.troncli4j.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.mywish.troncli4j.TronClient;
import io.mywish.troncli4j.model.EventResult;
import io.mywish.troncli4j.model.request.AccountRequest;
import io.mywish.troncli4j.model.request.BlockByIdRequest;
import io.mywish.troncli4j.model.request.BlockByNumRequest;
import io.mywish.troncli4j.model.request.Request;
import io.mywish.troncli4j.model.response.Error;
import io.mywish.troncli4j.model.response.*;
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
    private final URI fullNodeRpc;
    private final URI eventNodeRpc;
    private final ObjectMapper objectMapper;
    private Charset UTF8;

    public TronClientImpl(HttpClient client, URI fullNodeRpc, URI eventNodeRpc, ObjectMapper objectMapper) throws Exception {
        this.client = client;
        this.fullNodeRpc = fullNodeRpc;
        this.eventNodeRpc = eventNodeRpc;
        this.objectMapper = objectMapper;
        this.UTF8 = Charset.forName("UTF-8");
    }

    private <T extends Request> JsonNode doRequest(final String endpoint, final T request) throws Exception {
        HttpPost httpPost = new HttpPost(endpoint);
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
        HttpGet httpGet = new HttpGet(endpoint);
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
                doRequest(fullNodeRpc + "/wallet/getnodeinfo", null),
                NodeInfoResponse.class
        );
    }

    private BlockResponse parseBlock(JsonNode blockNode) throws Exception {
        return objectMapper.treeToValue(blockNode, BlockResponse.class);
    }

    @Override
    public BlockResponse getBlock(String id) throws Exception {
        return parseBlock(doRequest(fullNodeRpc + "/wallet/getblockbyid", new BlockByIdRequest(id)));
    }

    @Override
    public BlockResponse getBlock(Long number) throws Exception {
        return parseBlock(doRequest(fullNodeRpc + "/wallet/getblockbynum", new BlockByNumRequest(number)));
    }

    @Override
    public List<EventResult> getEventResult(String txId) throws Exception {
        return Arrays.asList(objectMapper.treeToValue(
                doRequest(eventNodeRpc + "/event/transaction/" + txId), EventResult[].class));
    }

    @Override
    public AccountResponse getAccount(String hexAddress) throws Exception {
        return objectMapper.treeToValue(
                doRequest(fullNodeRpc + "/wallet/getaccount", new AccountRequest(hexAddress)),
                AccountResponse.class);
    }
}
