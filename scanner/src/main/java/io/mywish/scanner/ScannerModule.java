package io.mywish.scanner;

import io.lastwill.eventscan.model.NetworkType;
import io.mywish.blockchain.WrapperModule;
import io.mywish.bot.integration.BotIntegrationModule;
import io.mywish.scanner.services.PendingTransactionService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.*;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

@Configuration
@ComponentScan
@PropertySource("classpath:scanner.properties")
@Import({WrapperModule.class, BotIntegrationModule.class})
public class ScannerModule {
    @Bean
    public static PropertySourcesPlaceholderConfigurer propertyConfig() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    @ConditionalOnBean(name = NetworkType.ETHEREUM_MAINNET_VALUE)
    @Bean
    public PendingTransactionService pendingTransactionServiceMain() {
        return new PendingTransactionService(NetworkType.ETHEREUM_MAINNET);
    }

    @ConditionalOnBean(name = NetworkType.ETHEREUM_ROPSTEN_VALUE)
    @Bean
    public PendingTransactionService pendingTransactionServiceRopsten() {
        return new PendingTransactionService(NetworkType.ETHEREUM_ROPSTEN);
    }
}
