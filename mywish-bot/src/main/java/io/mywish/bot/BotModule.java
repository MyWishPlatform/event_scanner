package io.mywish.bot;

import io.mywish.bot.service.MyWishBot;
import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.*;
import org.telegram.telegrambots.ApiContext;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.bots.DefaultBotOptions;

@ComponentScan
@Configuration
@PropertySource("classpath:module.properties")
public class BotModule {
    static {
        ApiContextInitializer.init();
    }

    @ConditionalOnProperty(name = "io.mywish.is-ros-com-nadzor", havingValue = "false", matchIfMissing = true)
    @Bean
    public TelegramBotsApi telegramBotsApi() {
        return new TelegramBotsApi();
    }

    @Bean
    public MyWishBot myWishBot() {
        final String host = "localhost";
        final int    port = 8123;

        HttpHost httpHost = new HttpHost(host, port);
        DefaultBotOptions botOptions = ApiContext.getInstance(DefaultBotOptions.class);
        botOptions.setRequestConfig(RequestConfig.custom().setProxy(httpHost).setAuthenticationEnabled(true).build());
        return new MyWishBot(botOptions);
    }
}
