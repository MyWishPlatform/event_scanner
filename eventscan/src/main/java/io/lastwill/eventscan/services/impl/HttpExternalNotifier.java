package io.lastwill.eventscan.services.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.lastwill.eventscan.messages.PaymentStatus;
import io.lastwill.eventscan.model.Contract;
import io.lastwill.eventscan.model.Product;
import io.lastwill.eventscan.services.ExternalNotifier;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.nio.client.HttpAsyncClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import io.lastwill.eventscan.messages.*;

import javax.annotation.PostConstruct;
import java.math.BigInteger;

@Slf4j
@ConditionalOnProperty("io.lastwill.eventscan.backend-url")
@Component
public class HttpExternalNotifier implements ExternalNotifier {

    @Autowired
    private HttpAsyncClient httpClient;

    @Autowired
    private ObjectMapper objectMapper;

    @Value("${io.lastwill.eventscan.backend-url}")
    private String baseUri;
    private String paymentUrl;
    private String repeatCheckUrl;
    private String deployed;
    private String killed;
    private String checkedUrl;
    private String triggeredUrl;

    @PostConstruct
    protected void init() {
        if (!baseUri.endsWith("/")) {
            baseUri = baseUri + "/";
        }
        paymentUrl = baseUri + "payment_notify";
        repeatCheckUrl = baseUri + "repeat_check";
        deployed = baseUri + "deployed_notify";
        killed = baseUri + "killed_notify";
        checkedUrl = baseUri + "checked_notify";
        triggeredUrl = baseUri + "triggered_notify";
    }

    @Override
    public void sendPaymentNotify(Product product, BigInteger balance, PaymentStatus status) {
        doPost(paymentUrl, new PaymentNotify(product.getId(), balance, status));
    }

    @Override
    public void sendCheckRepeatNotify(Contract contract) {
        doPost(repeatCheckUrl, new NotifyContract(contract.getId(), PaymentStatus.COMMITTED) {
            @Override
            public String getType() {
                return null;
            }
        });
    }

    @Override
    public void sendCheckedNotify(Contract contract, String transactionHash) {
        doPost(checkedUrl, new CheckedNotify(contract.getId(), transactionHash, PaymentStatus.COMMITTED));
    }

    @Override
    public void sendDeployedNotification(Product product, String address, String transactionHash, boolean committed, boolean status) {
        doPost(deployed, new ContractDeployedNotify(product.getId(), committed ? PaymentStatus.COMMITTED : PaymentStatus.REJECTED, address, transactionHash, status));
    }

    @Override
    public void sendKilledNotification(Contract contract) {
        doPost(killed, new ContractKilledNotify(contract.getId(), PaymentStatus.COMMITTED));
    }

    @Override
    public void sendTriggeredNotification(Contract contract) {
        doPost(triggeredUrl, new ContractTriggeredNotify(contract.getId(), PaymentStatus.COMMITTED));
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

}
