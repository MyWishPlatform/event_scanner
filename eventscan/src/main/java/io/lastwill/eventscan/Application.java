package io.lastwill.eventscan;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.ConnectionFactory;
import io.mywish.scanner.ScannerModule;
import io.mywish.scanner.model.NetworkType;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;

import java.net.URI;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

@SpringBootApplication
@Import({ScannerModule.class})
@EntityScan(basePackageClasses = {Application.class, Jsr310JpaConverters.class})
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

    @Bean(name = NetworkType.ETHEREUM_MAINNET_VALUE)
    @ConditionalOnProperty(name = "io.lastwill.eventscan.web3-url.ethereum")
    public Web3j web3jEthereum(@Value("${io.lastwill.eventscan.web3-url.ethereum}") String web3Url) {
        return Web3j.build(new HttpService(web3Url));
    }

    @Bean(name = NetworkType.ETHEREUM_ROPSTEN_VALUE)
    @ConditionalOnProperty(name = "io.lastwill.eventscan.web3-url.ropsten")
    public Web3j web3jRopsten(@Value("${io.lastwill.eventscan.web3-url.ropsten}") String web3Url) {
        return Web3j.build(new HttpService(web3Url));
    }

    @Bean(name = NetworkType.RSK_MAINNET_VALUE)
    @ConditionalOnProperty(name = "io.lastwill.eventscan.web3-url.rsk-mainnet")
    public Web3j web3jRsk(@Value("${io.lastwill.eventscan.web3-url.rsk-mainnet}") String web3Url) {
        return Web3j.build(new HttpService(web3Url));
    }

    @Bean(name = NetworkType.RSK_TESTNET_VALUE)
    @ConditionalOnProperty(name = "io.lastwill.eventscan.web3-url.rsk-testnet")
    public Web3j web3jRskTest(@Value("${io.lastwill.eventscan.web3-url.rsk-testnet}") String web3Url) {
        return Web3j.build(new HttpService(web3Url));
    }
}
