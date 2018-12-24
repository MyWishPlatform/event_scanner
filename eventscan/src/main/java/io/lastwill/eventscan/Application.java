package io.lastwill.eventscan;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.rabbitmq.client.ConnectionFactory;
import io.lastwill.eventscan.events.EventModule;
import io.mywish.scanner.ScannerModule;
import okhttp3.OkHttpClient;
import org.apache.http.client.config.CookieSpecs;
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
import org.springframework.scheduling.annotation.EnableScheduling;

import java.net.URI;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeUnit;

@SpringBootApplication
@Import({ScannerModule.class, EventModule.class})
@EntityScan(basePackageClasses = {Application.class, Jsr310JpaConverters.class})
@EnableScheduling
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
                                .setCookieSpec(CookieSpecs.STANDARD)
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
    public OkHttpClient okHttpClient(
            @Value("${io.lastwill.eventscan.backend.socket-timeout}") long socketTimeout,
            @Value("${io.lastwill.eventscan.backend.connection-timeout}") long connectionTimeout
    ) {
        return new OkHttpClient.Builder()
                .writeTimeout(socketTimeout, TimeUnit.MILLISECONDS)
                .readTimeout(socketTimeout, TimeUnit.MILLISECONDS)
                .connectTimeout(connectionTimeout, TimeUnit.MILLISECONDS)
                .build();
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper()
                // TODO: remove it!
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .registerModule(new JavaTimeModule());
    }
}
