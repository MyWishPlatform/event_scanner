package io.mywish.troncli4j.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.mywish.troncli4j.TronClient;
import io.mywish.troncli4j.model.request.BlockByIdRequest;
import io.mywish.troncli4j.model.request.BlockByNumRequest;
import io.mywish.troncli4j.model.request.Request;
import io.mywish.troncli4j.model.response.BlockResponse;
import io.mywish.troncli4j.model.response.Error;
import io.mywish.troncli4j.model.response.NodeInfoResponse;
import io.mywish.troncli4j.model.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;

import java.net.URI;
import java.nio.charset.Charset;

@Slf4j
public class TronClientImpl implements TronClient {
    private final HttpClient client;
    private final URI rpc;
    private final ObjectMapper objectMapper;
    private final String tcpHost;
    private final int tcpPort;
    private Charset UTF8;

    public TronClientImpl(URI tcpUrl, HttpClient client, URI rpc, ObjectMapper objectMapper) throws Exception {
        this.tcpHost = tcpUrl.getHost();
        this.tcpPort = tcpUrl.getPort();
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
//    public BalanceResponse getBalance(String code, String account) throws Exception {
//        JsonNode node = doRequest("/v1/chain/get_currency_balance", new BalanceRequest(code, account));
//        String[] unformatted = node.get(0).textValue().split(" ")[0].split("\\.");
//        return new BalanceResponse(new BigInteger(unformatted[0] + unformatted[1]), unformatted[1].length());
//    }
}
