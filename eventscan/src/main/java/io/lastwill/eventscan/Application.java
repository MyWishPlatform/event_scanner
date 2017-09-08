package io.lastwill.eventscan;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.config.ConnectionConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.apache.http.impl.nio.reactor.IOReactorConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;
import org.web3j.abi.EventEncoder;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;

@SpringBootApplication
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
                .setMaxConnPerRoute(10)
                .setMaxConnTotal(10)
                .setDefaultRequestConfig(
                        RequestConfig.custom()
                                .setConnectTimeout(connectionTimeout)
                                .setSocketTimeout(socketTimeout)
                                .setConnectionRequestTimeout(getConnectionTimeout)
                                .setExpectContinueEnabled(true)
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
                .setMaxConnPerRoute(10)
                .setMaxConnTotal(10)
                .setDefaultRequestConfig(
                        RequestConfig.custom()
                                .setConnectTimeout(connectionTimeout)
                                .setSocketTimeout(socketTimeout)
                                .setConnectionRequestTimeout(getConnectionTimeout)
                                .setExpectContinueEnabled(true)
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

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

    @Bean
    public Web3j web3j(CloseableHttpClient httpClient, @Value("${io.lastwill.eventscan.web3-url}") String web3Url) {
        return Web3j.build(new HttpService(
                web3Url,
                httpClient
        ));
    }
}
