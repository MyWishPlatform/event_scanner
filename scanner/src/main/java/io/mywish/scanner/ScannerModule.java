package io.mywish.scanner;

import io.lastwill.eventscan.model.NetworkType;
import io.mywish.bot.integration.BotIntegrationModule;
import io.mywish.scanner.services.PendingTransactionService;
import io.mywish.scanner.services.scanners.BtcScanner;
import io.mywish.scanner.services.LastBlockPersister;
import io.mywish.scanner.services.scanners.EosScanner;
import io.mywish.scanner.services.scanners.NeoScanner;
import io.mywish.scanner.services.scanners.Web3Scanner;
import io.mywish.wrapper.WrapperModule;
import io.mywish.wrapper.networks.BtcNetwork;
import io.mywish.wrapper.networks.EosNetwork;
import io.mywish.wrapper.networks.NeoNetwork;
import io.mywish.wrapper.networks.Web3Network;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
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

    @ConditionalOnBean(name = NetworkType.BTC_MAINNET_VALUE)
    @Bean
    public BtcScanner btcScannerMain(
            final @Qualifier(NetworkType.BTC_MAINNET_VALUE) BtcNetwork network,
            final @Value("${etherscanner.start-block-dir}") String dir,
            final @Value("${etherscanner.bitcoin.last-block.mainnet:#{null}}") Long lastBlock,
            final @Value("${etherscanner.bitcoin.polling-interval-ms}") Long pollingInterval,
            final @Value("${etherscanner.bitcoin.commit-chain-length}") Integer commitmentChainLength
    ) {
        return new BtcScanner(
                network,
                new LastBlockPersister(network.getType(), dir, lastBlock),
                pollingInterval,
                commitmentChainLength
        );
    }

    @ConditionalOnBean(name = NetworkType.BTC_TESTNET_3_VALUE)
    @Bean
    public BtcScanner btcScannerTest(
            final @Qualifier(NetworkType.BTC_TESTNET_3_VALUE) BtcNetwork network,
            final @Value("${etherscanner.start-block-dir}") String dir,
            final @Value("${etherscanner.bitcoin.last-block.testnet:#{null}}") Long lastBlock,
            final @Value("${etherscanner.bitcoin.polling-interval-ms}") Long pollingInterval,
            final @Value("${etherscanner.bitcoin.commit-chain-length}") Integer commitmentChainLength
    ) {
        return new BtcScanner(
                network,
                new LastBlockPersister(network.getType(), dir, lastBlock),
                pollingInterval,
                commitmentChainLength
        );
    }

    @ConditionalOnBean(name = NetworkType.EOS_MAINNET_VALUE)
    @Bean
    public EosScanner eosScannerMain(
            final @Qualifier(NetworkType.EOS_MAINNET_VALUE) EosNetwork network,
            final @Value("${etherscanner.start-block-dir}") String dir,
            final @Value("${etherscanner.eos.last-block.mainnet:#{null}}") Long lastBlock
    ) {
        return new EosScanner(
                network,
                new LastBlockPersister(network.getType(), dir, lastBlock)
        );
    }

    @ConditionalOnBean(name = NetworkType.EOS_TESTNET_VALUE)
    @Bean
    public EosScanner eosScannerTest(
            final @Qualifier(NetworkType.EOS_TESTNET_VALUE) EosNetwork network,
            final @Value("${etherscanner.start-block-dir}") String dir,
            final @Value("${etherscanner.eos.last-block.testnet:#{null}}") Long lastBlock
    ) {
        return new EosScanner(
                network,
                new LastBlockPersister(network.getType(), dir, lastBlock)
        );
    }

    @ConditionalOnBean(name = NetworkType.NEO_MAINNET_VALUE)
    @Bean
    public NeoScanner neoScannerMain(
            final @Qualifier(NetworkType.NEO_MAINNET_VALUE) NeoNetwork network,
            final @Value("${etherscanner.start-block-dir}") String dir,
            final @Value("${etherscanner.neo.last-block.mainnet:#{null}}") Long lastBlock,
            final @Value("${etherscanner.neo.polling-interval-ms}") Long pollingInterval,
            final @Value("${etherscanner.neo.commit-chain-length}") Integer commitmentChainLength
    ) {
        return new NeoScanner(
                network,
                new LastBlockPersister(network.getType(), dir, lastBlock),
                pollingInterval,
                commitmentChainLength
        );
    }

    @ConditionalOnBean(name = NetworkType.NEO_TESTNET_VALUE)
    @Bean
    public NeoScanner neoScannerTest(
            final @Qualifier(NetworkType.NEO_TESTNET_VALUE) NeoNetwork network,
            final @Value("${etherscanner.start-block-dir}") String dir,
            final @Value("${etherscanner.neo.last-block.testnet:#{null}}") Long lastBlock,
            final @Value("${etherscanner.neo.polling-interval-ms}") Long pollingInterval,
            final @Value("${etherscanner.neo.commit-chain-length}") Integer commitmentChainLength
    ) {
        return new NeoScanner(
                network,
                new LastBlockPersister(network.getType(), dir, lastBlock),
                pollingInterval,
                commitmentChainLength
        );
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

    @ConditionalOnBean(name = NetworkType.ETHEREUM_MAINNET_VALUE)
    @Bean
    public Web3Scanner ethScannerMain(
            final @Qualifier(NetworkType.ETHEREUM_MAINNET_VALUE) Web3Network network,
            final @Value("${etherscanner.start-block-dir}") String dir,
            final @Value("${etherscanner.polling-interval-ms:5000}") Long pollingInterval,
            final @Value("${etherscanner.commit-chain-length:5}") Integer commitmentChainLength
    ) {
        return new Web3Scanner(
                network,
                new LastBlockPersister(network.getType(), dir, null),
                pollingInterval,
                commitmentChainLength
        );
    }

    @ConditionalOnBean(name = NetworkType.ETHEREUM_ROPSTEN_VALUE)
    @Bean
    public Web3Scanner ethScannerRopsten(
            final @Qualifier(NetworkType.ETHEREUM_ROPSTEN_VALUE) Web3Network network,
            final @Value("${etherscanner.start-block-dir}") String dir,
            final @Value("${etherscanner.polling-interval-ms:5000}") Long pollingInterval,
            final @Value("${etherscanner.commit-chain-length:5}") Integer commitmentChainLength
    ) {
        return new Web3Scanner(
                network,
                new LastBlockPersister(network.getType(), dir, null),
                pollingInterval,
                commitmentChainLength
        );
    }

    @ConditionalOnBean(name = NetworkType.RSK_MAINNET_VALUE)
    @Bean
    public Web3Scanner rskScannerMain(
            final @Qualifier(NetworkType.RSK_MAINNET_VALUE) Web3Network network,
            final @Value("${etherscanner.start-block-dir}") String dir,
            final @Value("${etherscanner.polling-interval-ms:5000}") Long pollingInterval,
            final @Value("${etherscanner.commit-chain-length:5}") Integer commitmentChainLength
    ) {
        return new Web3Scanner(
                network,
                new LastBlockPersister(network.getType(), dir, null),
                pollingInterval,
                commitmentChainLength
        );
    }

    @ConditionalOnBean(name = NetworkType.RSK_TESTNET_VALUE)
    @Bean
    public Web3Scanner rskScannerTest(
            final @Qualifier(NetworkType.RSK_TESTNET_VALUE) Web3Network network,
            final @Value("${etherscanner.start-block-dir}") String dir,
            final @Value("${etherscanner.polling-interval-ms:5000}") Long pollingInterval,
            final @Value("${etherscanner.commit-chain-length:5}") Integer commitmentChainLength
    ) {
        return new Web3Scanner(
                network,
                new LastBlockPersister(network.getType(), dir, null),
                pollingInterval,
                commitmentChainLength
        );
    }
}
