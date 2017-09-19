package io.lastwill.eventscan.services.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import io.lastwill.eventscan.messages.*;
import io.lastwill.eventscan.model.Contract;
import io.lastwill.eventscan.services.ExternalNotifier;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.concurrent.TimeoutException;

@Slf4j
@ConditionalOnProperty("io.lastwill.eventscan.backend-mq.url")
@Component
public class MQExternalNotifier implements ExternalNotifier {
    private final static String contentType = "application/json";
    @Autowired
    private ConnectionFactory factory;

    @Autowired
    private ObjectMapper objectMapper;

    @Value("${io.lastwill.eventscan.backend-mq.queue}")
    private String queueName;

    @Value("${io.lastwill.eventscan.backend-mq.ttl-ms}")
    private long messageTTL;

    @Value("${io.lastwill.eventscan.backend-mq.init:false}")
    private boolean initQueue = false;

    private Connection connection;
    private Channel channel;

    @PostConstruct
    protected void init() throws IOException, TimeoutException {
        connection = factory.newConnection();
        channel = connection.createChannel();
        if (initQueue) {
            channel.queueUnbind(queueName, queueName, queueName);
            channel.queueDelete(queueName);
            channel.exchangeDelete(queueName);
        }
        channel.exchangeDeclare(queueName, "direct", true);
        channel.queueDeclare(queueName, true, false, false,  new HashMap<>());
        channel.queueBind(queueName, queueName, queueName);

        byte[] pingJson = objectMapper.writeValueAsBytes(new Ping());

        channel.basicPublish(
                queueName,
                queueName,
                new AMQP.BasicProperties.Builder()
                        .contentType(contentType)
                        .expiration("60000")
                        .type("ping")
                .build(),
                pingJson
        );
    }

    @PreDestroy
    protected void close() {
        if (channel != null) {
            try {
                channel.close();
            }
            catch (TimeoutException | IOException e) {
                log.error("Closing channel failed.", e);
            }
        }
        if (connection != null) {
            try {
                connection.close();
            }
            catch (IOException e) {
                log.error("Closing queue connection failed.", e);
            }
        }
    }

    protected synchronized void send(NotifyContract notify) {
        try {
            byte[] json = objectMapper.writeValueAsBytes(notify);

            channel.basicPublish(
                    queueName,
                    queueName,
                    new AMQP.BasicProperties.Builder()
                            .type(notify.getType())
                            .contentType(contentType)
                            .expiration(String.valueOf(messageTTL))
                            .build(),
                    json
            );
            log.debug("Send notification type '{}' about contract {} to queue '{}'.", notify.getType(), notify.getContractId(), queueName);
        }
        catch (JsonProcessingException e) {
            log.error("Error on serializing message {}.", notify, e);
        }
        catch (IOException e) {
            log.error("Error on sending message {}.", notify, e);
        }

    }

    @Override
    public void sendPaymentNotify(Contract contract, BigInteger balance, PaymentStatus status) {
        send(new PaymentNotify(contract.getId(), balance, status));
    }

    @Override
    public void sendCheckRepeatNotify(Contract contract) {
        send(new RepeatCheckNotify(contract.getId(), PaymentStatus.COMMITTED));
    }

    @Override
    public void sendCheckedNotify(Contract contract, String transactionHash) {
        send(new CheckedNotify(contract.getId(), transactionHash, PaymentStatus.COMMITTED));
    }

    @Override
    public void sendDeployedNotification(Contract contract, String address, String transactionHash, boolean committed) {
        send(new ContractDeployedNotify(contract.getId(), committed ? PaymentStatus.COMMITTED : PaymentStatus.REJECTED, address, transactionHash));
    }

    @Override
    public void sendKilledNotification(Contract contract) {
        send(new ContractKilledNotify(contract.getId(), PaymentStatus.COMMITTED));
    }

    @Override
    public void sendTriggeredNotification(Contract contract) {
        send(new ContractTriggeredNotify(contract.getId(), PaymentStatus.COMMITTED));
    }
}
