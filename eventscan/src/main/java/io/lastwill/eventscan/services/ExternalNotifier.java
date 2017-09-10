package io.lastwill.eventscan.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.lastwill.eventscan.model.Contract;
import lombok.Getter;
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

import javax.annotation.PostConstruct;
import java.math.BigInteger;
import java.net.URL;

@Slf4j
@Component
public class ExternalNotifier {
    public enum PaymentStatus {
        CREATED,
        COMMITTED,
        REJECTED
    }

    @Autowired
    private HttpAsyncClient httpClient;

    @Autowired
    private ObjectMapper objectMapper;

    @Value("${io.lastwill.eventscan.backend-url}")
    private String baseUri;
    private String paymentUrl;
    private String repeatCheckUrl;

    @PostConstruct
    protected void init() {
        if (!baseUri.endsWith("/")) {
            baseUri = baseUri + "/";
        }
        paymentUrl = baseUri + "payment_notify";
        repeatCheckUrl = baseUri + "repeat_check";
    }

    public void sendPaymentNotify(Contract contract, BigInteger balance, PaymentStatus status) {
        doPost(paymentUrl, new PaymentNotify(contract.getId(), balance, status));
    }

    public void sendCheckRepeatNotify(Contract contract) {
        doPost(repeatCheckUrl, new NotifyContract(contract.getId()));
    }

    private void doPost(final String uri, final Object object) {
        HttpPost post = new HttpPost(uri);
        byte[] buf;
        try {
            buf = objectMapper.writeValueAsBytes(object);
        }
        catch (JsonProcessingException e) {
            log.error("Serializing class {} ({}) failed.", object.getClass(), object, e);
            return;
        }
        post.setEntity(new ByteArrayEntity(buf, ContentType.APPLICATION_JSON));
        log.debug("Sending notification to {}, {} bytes.", uri, buf.length);
        httpClient.execute(post, new FutureCallback<HttpResponse>() {
            @Override
            public void completed(HttpResponse result) {
                if (result.getStatusLine().getStatusCode() != 200) {
                    log.warn("Sending notification to {} result code {} != 200.", uri, result.getStatusLine().getStatusCode());
                    return;
                }
                log.debug("Sending notification to {} completed with code 200.", uri);
            }

            @Override
            public void failed(Exception ex) {
                log.error("Sending notification to {} failed.", uri, ex);
            }

            @Override
            public void cancelled() {
                log.error("Sending notification to {} canceled.", uri);
            }
        });

    }

    @Getter
    @RequiredArgsConstructor
    public static class NotifyContract {
        private final int contractId;
    }

    @Getter
    public static class PaymentNotify extends NotifyContract {
        private final BigInteger balance;
        private final PaymentStatus state;

        public PaymentNotify(int contractId, BigInteger balance, PaymentStatus state) {
            super(contractId);
            this.balance = balance;
            this.state = state;
        }
    }
}
