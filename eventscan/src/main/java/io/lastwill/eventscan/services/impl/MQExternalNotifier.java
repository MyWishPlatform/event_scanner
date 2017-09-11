package io.lastwill.eventscan.services.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.lastwill.eventscan.messages.ContractDeployed;
import io.lastwill.eventscan.messages.NotifyContract;
import io.lastwill.eventscan.messages.PaymentNotify;
import io.lastwill.eventscan.messages.PaymentStatus;
import io.lastwill.eventscan.model.Contract;
import io.lastwill.eventscan.services.ExternalNotifier;
import lombok.extern.slf4j.Slf4j;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.jms.*;
import java.math.BigInteger;

@Slf4j
@ConditionalOnProperty("io.lastwill.eventscan.backend-mq.url")
@Component
public class MQExternalNotifier implements ExternalNotifier {
    private final static String MessageTypeProperty = "Type";
    @Autowired
    @Qualifier("backendAMQ")
    private ActiveMQConnectionFactory factory;

    @Autowired
    private ObjectMapper objectMapper;

    @Value("${io.lastwill.eventscan.backend-mq.queue}")
    private String queueName;

    @Value("${io.lastwill.eventscan.backend-mq.ttl-ms}")
    private long messageTTL;

    private Connection connection;
    private Session session;
    private MessageProducer messageProducer;

    @PostConstruct
    protected void init() throws JMSException {
        connection = factory.createConnection();
        connection.start();

        session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        Queue queue = session.createQueue(queueName);
        messageProducer = session.createProducer(queue);
        messageProducer.setDeliveryMode(DeliveryMode.PERSISTENT);
        messageProducer.setTimeToLive(messageTTL);

        TextMessage message = session.createTextMessage("Ping");
        message.setStringProperty("Type", "Ping");
        messageProducer.send(message);
//        session.createTextMessage().setStringProperty("Type", "payment");
//        messageProducer.send();
//        session.createTextMessage().setStringProperty();
    }

    @PreDestroy
    protected void close() {
        if (session != null) {
            try {
                session.close();
            }
            catch (JMSException e) {
                log.error("Closing JMS session failed.", e);
            }
        }
        if (connection != null) {
            try {
                connection.close();
            }
            catch (JMSException e) {
                log.error("Closing JMS connection failed.", e);
            }
        }
    }

    protected void send(NotifyContract notify) {
        try {
            String json = objectMapper.writeValueAsString(notify);

            TextMessage textMessage = session.createTextMessage();
            textMessage.setStringProperty(MessageTypeProperty, notify.getType());
            textMessage.setText(json);

            messageProducer.send(textMessage);
        }
        catch (JMSException e) {
            log.error("Error on sending message {}.", notify, e);
        }
        catch (JsonProcessingException e) {
            log.error("Error on serializing message {}.", notify, e);
        }

    }

    @Override
    public void sendPaymentNotify(Contract contract, BigInteger balance, PaymentStatus status) {
        send(new PaymentNotify(contract.getId(), balance, status));
    }

    @Override
    public void sendCheckRepeatNotify(Contract contract) {
        send(new NotifyContract(contract.getId(), PaymentStatus.COMMITTED) {
            @Override
            public String getType() {
                return "repeatCheck";
            }
        });
    }

    @Override
    public void sendDeployedNotification(Contract contract, String address, String transactionHash, boolean committed) {
        send(new ContractDeployed(contract.getId(), committed ? PaymentStatus.COMMITTED : PaymentStatus.REJECTED, address, transactionHash));
    }

    @Override
    public void sendKilledNotification(Contract contract) {
        send(new NotifyContract(contract.getId(), PaymentStatus.COMMITTED) {
            @Override
            public String getType() {
                return "killed";
            }
        });
    }
}
