package io.mywish.wavescli4j.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.mywish.wavescli4j.WavesClient;
import io.mywish.wavescli4j.model.Block;
import io.mywish.wavescli4j.model.Height;
import io.mywish.wavescli4j.model.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;

import java.nio.charset.Charset;

@Slf4j
@RequiredArgsConstructor
public class WavesClientImpl implements WavesClient {
    private static final Charset UTF8 = Charset.forName("UTF-8");
    private final HttpClient client;
    private final String rpc;
    private final ObjectMapper objectMapper;

    @Override
    public Height getHeight() throws Exception {
        return get("/blocks/height", Height.class);
    }

    @Override
    public Block getBlock(Long number) throws Exception {
        return get("/blocks/at/" + number, Block.class);
    }

    private <T extends Response> T get(String request, Class<T> tClass) throws Exception {
        return objectMapper.treeToValue(doRequest(request), tClass);
    }

    private JsonNode doRequest(final String endpoint) throws Exception {
        HttpGet request = new HttpGet(rpc + endpoint);
        HttpResponse response = client.execute(request);
        HttpEntity entity = response.getEntity();
        String responseBody = EntityUtils.toString(entity, UTF8);
        return objectMapper.readTree(responseBody);
    }
}
