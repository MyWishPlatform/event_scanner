package io.mywish.scanner;

import io.lastwill.eventscan.model.NetworkType;
import io.mywish.blockchain.WrapperModule;
import io.mywish.scanner.services.PendingTransactionService;
import org.springframework.context.annotation.*;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

@Configuration
@ComponentScan
@PropertySource("classpath:scanner.properties")
@Import(WrapperModule.class)
public class ScannerModule {
    @Bean
    public static PropertySourcesPlaceholderConfigurer propertyConfig() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    @Bean
    public PendingTransactionService pendingTransactionServiceMain() {
        return new PendingTransactionService(NetworkType.ETHEREUM_MAINNET);
    }

    @Bean
    public PendingTransactionService pendingTransactionServiceRopsten() {
        return new PendingTransactionService(NetworkType.ETHEREUM_ROPSTEN);
    }
}
