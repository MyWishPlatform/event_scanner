package io.lastwill.eventscan.services.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.*;
import io.lastwill.eventscan.model.NetworkType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

@Component
public class MQConsumer {
    private final static String contentType = "application/json";
    @Autowired
    private ConnectionFactory factory;

    @Autowired
    private ObjectMapper objectMapper;

//    @Value("${}")
    private String queueName= "notification-swaps-mainnet";

    private Connection connection;
    private Channel channel;

    @Getter
    public static class Msg {
        private String status;
        private Integer contractId;
    }

    @PostConstruct
    public void init() throws IOException, TimeoutException {
        connection = factory.newConnection();
        channel = connection.createChannel();

        channel.basicConsume(queueName, true, new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
               if (!"launch".equalsIgnoreCase(properties.getType())) {
                   return;
               }
                super.handleDelivery(consumerTag, envelope, properties, body);
            }
        });
    }
}
