package io.mywish.dream;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.Web3jService;
import org.web3j.protocol.admin.Admin;
import org.web3j.protocol.http.HttpService;

@PropertySource("classpath:application.properties")
@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        new SpringApplicationBuilder()
                .addCommandLineProperties(true)
                .web(true)
                .sources(Application.class)
                .main(Application.class)
                .run(args);
    }

    @Bean
    public Admin admin(@Value("${io.mywish.dream.web3-url}") String web3Url) {
        return Admin.build(new HttpService(web3Url));
    }

    @Bean
    public Web3j web3j(@Value("${io.mywish.dream.web3-url}") String web3Url) {
        return Web3j.build(new HttpService(web3Url));
    }
}
