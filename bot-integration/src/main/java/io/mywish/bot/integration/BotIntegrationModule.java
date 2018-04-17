package io.mywish.bot.integration;

import io.mywish.bot.BotModule;
import io.mywish.bot.integration.services.impl.BlockchainInfoExplorer;
import io.mywish.bot.integration.services.impl.EtherescanExplorer;
import io.mywish.bot.integration.services.impl.RskExplorer;
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
}
