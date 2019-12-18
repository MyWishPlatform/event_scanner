package io.mywish.bot.integration;

import io.mywish.bot.BotModule;
import io.mywish.bot.integration.services.BotIntegration;
import io.mywish.bot.integration.services.BotIntegrationLight;
import io.mywish.bot.integration.services.impl.*;
import io.mywish.bot.service.MyWishBot;
import io.mywish.bot.service.MyWishBotLight;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.*;

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
    public NeoscanExplorer neoscanExplorerTestnet() {
        return new NeoscanExplorer(true);
    }

    @Bean
    public TronscanExplorer tronscanExplorerTestnet() {
        return new TronscanExplorer(true);
    }

    @Bean
    public TronscanExplorer tronscanExplorer() {
        return new TronscanExplorer(false);
    }

    @Bean
    public BloksExplorer bloksExplorerTestnet() {
        return new BloksExplorer(true);
    }

    @Bean
    public BloksExplorer bloksExplorer() {
        return new BloksExplorer(false);
    }

    @Bean
    public WavesExplorer wavesExplorerTestnet() {
        return new WavesExplorer(true);
    }

    @Bean
    public WavesExplorer wavesExplorer() {
        return new WavesExplorer(false);
    }

    @Bean
    public BinanceExplorer binanceExplorerTestnet() {
        return new BinanceExplorer(true);
    }

    @Bean
    public BinanceExplorer binanceExplorer() {
        return new BinanceExplorer(false);
    }

    @Bean
    @ConditionalOnBean(MyWishBot.class)
    public BotIntegration botIntegration() {
        return new BotIntegration();
    }

    @Bean
    @ConditionalOnBean(MyWishBotLight.class)
    public BotIntegrationLight botIntegrationLight() {
        return new BotIntegrationLight();
    }
}
