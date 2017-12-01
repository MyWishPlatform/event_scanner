package io.lastwill.eventscan;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.ConnectionFactory;
import io.mywish.bot.BotModule;
import io.mywish.scanner.ScannerModule;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.apache.http.impl.nio.reactor.IOReactorConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;

import java.net.URI;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

@SpringBootApplication
@Import({BotModule.class, ScannerModule.class})
public class Application {
    public static void main(String[] args) {
        new SpringApplicationBuilder()
                .addCommandLineProperties(true)
                .web(false)
                .sources(Application.class)
                .main(Application.class)
                .run(args);

    }

    @Bean(destroyMethod = "close")
    public CloseableHttpClient closeableHttpClient(
            @Value("${io.lastwill.eventscan.backend.get-connection-timeout}") int getConnectionTimeout,
            @Value("${io.lastwill.eventscan.backend.connection-timeout}") int connectionTimeout,
            @Value("${io.lastwill.eventscan.backend.socket-timeout}") int socketTimeout) {

        return HttpClientBuilder
                .create()
                .setMaxConnPerRoute(50)
                .setMaxConnTotal(200)
                .setDefaultRequestConfig(
                        RequestConfig.custom()
                                .setConnectTimeout(connectionTimeout)
                                .setSocketTimeout(socketTimeout)
                                .setConnectionRequestTimeout(getConnectionTimeout)
                                .build()
                )
                .setConnectionManagerShared(true)
                .build();
    }

    @Bean(destroyMethod = "close")
    public CloseableHttpAsyncClient closeableHttpAsyncClient(
            @Value("${io.lastwill.eventscan.backend.get-connection-timeout}") int getConnectionTimeout,
            @Value("${io.lastwill.eventscan.backend.connection-timeout}") int connectionTimeout,
            @Value("${io.lastwill.eventscan.backend.socket-timeout}") int socketTimeout) {

        System.setProperty("http.conn-manager.timeout", "1000");
        CloseableHttpAsyncClient asyncClient = HttpAsyncClientBuilder
                .create()
                .useSystemProperties()
                .setMaxConnPerRoute(50)
                .setMaxConnTotal(100)
                .setDefaultRequestConfig(
                        RequestConfig.custom()
                                .setConnectTimeout(connectionTimeout)
                                .setSocketTimeout(socketTimeout)
                                .setConnectionRequestTimeout(getConnectionTimeout)
                                .build()
                )
                .setDefaultIOReactorConfig(
                        IOReactorConfig.custom()
                                .setConnectTimeout(connectionTimeout)
                                .setSoTimeout(socketTimeout)
                                .build()
                )
                .build();
        asyncClient.start();
        return asyncClient;
    }

    @ConditionalOnProperty("io.lastwill.eventscan.backend-mq.url")
    @Bean
    public ConnectionFactory connectionFactory(@Value("${io.lastwill.eventscan.backend-mq.url}") URI uri) throws NoSuchAlgorithmException, KeyManagementException, URISyntaxException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setUri(uri);
        factory.setAutomaticRecoveryEnabled(true);
        return factory;
    }


    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

    @Bean
    public Web3j web3j(@Value("${io.lastwill.eventscan.web3-url}") String web3Url) {
        return Web3j.build(new HttpService(web3Url));
    }
}
