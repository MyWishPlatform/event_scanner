package io.lastwill.eventscan.services.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.*;
import io.lastwill.eventscan.events.model.SwapsNotificationMQEvent;
import io.lastwill.eventscan.model.Swap2Json;
import io.lastwill.eventscan.model.Swaps2Order;
import io.lastwill.eventscan.repositories.Swaps2OrderRepository;
import io.lastwill.eventscan.repositories.UserRepository;
import io.mywish.scanner.services.EventPublisher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.concurrent.TimeoutException;


@Slf4j
public class MQConsumer {

    @Autowired
    private ConnectionFactory factory;

    @Value("${io.lastwill.eventscan.backend-mq.queue.swaps2}")
    private String queueName;

    private Channel channel;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private Swaps2OrderRepository swaps2OrderRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EventPublisher eventPublisher;

    @PostConstruct
    public void init() throws IOException, TimeoutException {
        Connection connection = factory.newConnection();
        channel = connection.createChannel();

        channel.basicConsume(queueName, true, new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) {
                if (!"launch".equalsIgnoreCase(properties.getType())) {
                    return;
                }
                onConvert(new String(body));
            }
        });
    }

    private void onConvert(String queueMessage) {
        Swap2Json swap2Json = null;
        try {
            swap2Json = mapper.readValue(queueMessage, Swap2Json.class);
        } catch (IOException e) {
            log.error("Can't read json value from {}", queueMessage);
            e.printStackTrace();
            return;
        }
        Swaps2Order swaps2Order = swaps2OrderRepository.findOne(Integer.parseInt(swap2Json.getContractId()));
        if (swaps2Order == null) {
            log.warn("Can't find swaps2 order by id = {}", swap2Json.getContractId());
        } else {
            eventPublisher.publish(new SwapsNotificationMQEvent(swaps2Order, userRepository.findOne(swaps2Order.getUser())));
            log.info("Added new swap2 order from data base {}.", swaps2Order.getOrderId());
        }
    }
}
