package io.mywish.bot.integration;

import io.mywish.bot.BotModule;
import io.mywish.bot.integration.services.BotIntegration;
import io.mywish.bot.integration.services.impl.BlockchainInfoExplorer;
import io.mywish.bot.integration.services.impl.EtherescanExplorer;
import io.mywish.bot.integration.services.impl.RskExplorer;
import io.mywish.bot.service.MyWishBot;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.*;
import org.springframework.stereotype.Component;

@ComponentScan
@Configuration
@PropertySource("classpath:bot-integration.properties")
@Import(BotModule.class)
public class BotIntegrationModule {
    @Bean
    public EtherescanExplorer etherescanExplorerTestnet() {
        return new EtherescanExplorer(true);
    }

    @Bean
    public EtherescanExplorer etherescanExplorer() {
        return new EtherescanExplorer(false);
    }

    @Bean
    public RskExplorer rskExplorerTestnet() {
        return new RskExplorer(true);
    }

    @Bean
    public RskExplorer rskExplorer() {
        return new RskExplorer(false);
    }

    @Bean
    public BlockchainInfoExplorer blockchainInfoExplorerTestnet() {
        return new BlockchainInfoExplorer(true);
    }

    @Bean
    public BlockchainInfoExplorer blockchainInfoExplorer() {
        return new BlockchainInfoExplorer(false);
    }

    @Bean
    @ConditionalOnBean(MyWishBot.class)
    public BotIntegration botIntegration() {
        return new BotIntegration();
    }
}
