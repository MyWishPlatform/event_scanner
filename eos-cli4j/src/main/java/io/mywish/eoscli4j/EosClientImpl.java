package io.mywish.eoscli4j;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.mywish.eoscli4j.model.request.BalanceRequest;
import io.mywish.eoscli4j.model.request.BlockRequest;
import io.mywish.eoscli4j.model.request.Request;
import io.mywish.eoscli4j.model.response.BalanceResponse;
import io.mywish.eoscli4j.model.response.BlockResponse;
import io.mywish.eoscli4j.model.response.ChainInfoResponse;
import io.mywish.eoscli4j.model.response.Error;
import io.mywish.eoscli4j.model.response.Response;
import io.mywish.eoscli4j.service.TcpClient;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;

import java.math.BigInteger;
import java.net.URI;
import java.nio.charset.Charset;

public class EosClientImpl implements EosClient {
    private final TcpClient tcpClient;
    private final HttpClient client;
    private final URI rpc;
    private final ObjectMapper objectMapper;
    private Charset UTF8;

    public EosClientImpl(String tcpHost, int tcpPort, HttpClient client, URI rpc, ObjectMapper objectMapper) throws Exception {
        this.tcpClient = new TcpClient(tcpHost, tcpPort);
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
    public ChainInfoResponse getChainInfo() throws Exception {
        return objectMapper.treeToValue(
                doRequest("/v1/chain/get_info", null),
                ChainInfoResponse.class
        );
    }

    @Override
    public BlockResponse getBlock(String hash) throws Exception {
        return objectMapper.treeToValue(
                doRequest("/v1/chain/get_block", new BlockRequest(hash)),
                BlockResponse.class
        );
    }

    @Override
    public BlockResponse getBlock(Long number) throws Exception {
        return objectMapper.treeToValue(
                doRequest("/v1/chain/get_block", new BlockRequest(number)),
                BlockResponse.class
        );
    }

    @Override
    public BalanceResponse getBalance(String code, String account) throws Exception {
        JsonNode node = doRequest("/v1/chain/get_currency_balance", new BalanceRequest(code, account));
        String[] unformatted = node.get(0).textValue().split(" ")[0].split("\\.");
        return new BalanceResponse(new BigInteger(unformatted[0] + unformatted[1]), unformatted[1].length());
    }

    @Override
    public void subscribe(long lastBlock, BlockCallback callback) throws Exception {
        this.tcpClient.write("s" + String.valueOf(lastBlock));
        while (true) {
            int length = this.tcpClient.readInt();
            String blockJSON = this.tcpClient.readString(length);
            BlockResponse block = objectMapper.readValue(blockJSON, BlockResponse.class);
            callback.callback(block);
        }
    }
}
