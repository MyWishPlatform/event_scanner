package io.lastwill;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;

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
    public CloseableHttpClient closeableHttpClient() {
        return HttpClientBuilder.create()
                .setMaxConnPerRoute(10)
                .setMaxConnTotal(10)
                .setConnectionManagerShared(true)
                .build();
    }
}
