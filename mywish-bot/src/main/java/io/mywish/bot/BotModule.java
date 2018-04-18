package io.mywish.bot;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.*;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;

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
}
