package io.mywish.bot;

import io.mywish.bot.service.*;
import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.*;
import org.telegram.telegrambots.ApiContext;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.bots.DefaultBotOptions;

@ComponentScan
@Configuration
@PropertySource("classpath:mywish-bot.properties")
public class BotModule {
    static {
        ApiContextInitializer.init();
    }

    @Value("${io.mywish.bot.http-proxy:#{null}}")
    private String proxy;


    @ConditionalOnProperty(name = "io.mywish.is-ros-com-nadzor", havingValue = "false", matchIfMissing = true)
    @Bean
    @Scope("prototype")
    public TelegramBotsApi telegramBotsApi() {
        return new TelegramBotsApi();
    }

    @ConditionalOnProperty(name = "io.mywish.is-ros-com-nadzor", havingValue = "false", matchIfMissing = true)
    @Bean
    public MyWishBot myWishBot() {
        DefaultBotOptions botOptions = ApiContext.getInstance(DefaultBotOptions.class);
        if (proxy != null) {
            botOptions.setRequestConfig(
                    RequestConfig
                            .custom()
                            .setProxy(HttpHost.create(proxy))
                            .setAuthenticationEnabled(false)
                            .build()
            );
        }
        return new MyWishBot(botOptions);
    }

    @ConditionalOnProperty(name = "io.mywish.is-ros-com-nadzor", havingValue = "false", matchIfMissing = true)
    @Bean
    public MyWishBotLight myWishBotLight() {
        DefaultBotOptions botOptions = ApiContext.getInstance(DefaultBotOptions.class);
        if (proxy != null) {
            botOptions.setRequestConfig(
                    RequestConfig
                            .custom()
                            .setProxy(HttpHost.create(proxy))
                            .setAuthenticationEnabled(false)
                            .build()
            );
        }
        return new MyWishBotLight(botOptions);
    }

    @ConditionalOnProperty(name = "io.mywish.bot.db-persister", havingValue = "true")
    @Bean
    public ChatPersister chatPersister() {
        return new ChatDbPersister();
    }

    @ConditionalOnProperty(name = "io.mywish.bot.db-persister", havingValue = "false", matchIfMissing = true)
    @Bean
    public ChatPersister chatFilePersister(@Value("${io.mywish.bot.file:#{null}}") String fileName) {
        ChatFilePersister chatFilePersister = new ChatFilePersister(fileName);
        return chatFilePersister;
    }

    @ConditionalOnProperty(name = "io.mywish.bot.db-persister", havingValue = "false", matchIfMissing = true)
    @Bean
    public ChatPersister chatFileLightPersister(@Value("${io.mywish.bot.light.file:#{null}}") String fileName) {
        ChatFilePersister chatFileLightPersister = new ChatFilePersister(fileName);
        return chatFileLightPersister;
    }
}
