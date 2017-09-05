package io.lastwill.eventscan.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.nio.client.HttpAsyncClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigInteger;

@Slf4j
@Component
public class ExternalNotifier {
    @Autowired
    private HttpAsyncClient httpClient;

    @Autowired
    private ObjectMapper objectMapper;

    @Value("${io.lastwill.eventscan.backend-url}")
    private String baseUri;

    public void sendNotify(String address, BigInteger amount, int blockCommitted) {
        HttpPost post = new HttpPost(baseUri);
        byte[] buf;
        try {
            buf = objectMapper.writeValueAsBytes(new NotifyContract(address, amount, blockCommitted));
        }
        catch (JsonProcessingException e) {
            log.error("Sending notify failed during serialization process.", e);
            return;
        }
        post.setEntity(new ByteArrayEntity(buf, ContentType.APPLICATION_JSON));
        httpClient.execute(post, new FutureCallback<HttpResponse>() {
            @Override
            public void completed(HttpResponse result) {
                if (result.getStatusLine().getStatusCode() != 200) {
                    log.warn("Sending notification result code {} != 200.", result.getStatusLine().getStatusCode());
                    return;
                }
                log.debug("Sending notification completed with code 200.");
            }

            @Override
            public void failed(Exception ex) {
                log.error("Sending notification failed.", ex);
            }

            @Override
            public void cancelled() {
                log.error("Sending notification canceled.");
            }
        });
    }

    @RequiredArgsConstructor
    public static class NotifyContract {
        private final String address;
        private final BigInteger amount;
        private final int blockCommitted;
    }
}
