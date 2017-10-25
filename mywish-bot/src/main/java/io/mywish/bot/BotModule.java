package io.mywish.bot;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.*;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;

@ComponentScan
@Configuration
@PropertySource("module.properties")
public class BotModule {
    static {
        ApiContextInitializer.init();
    }

    @Bean
    public TelegramBotsApi telegramBotsApi() {
        return new TelegramBotsApi();
    }
}
