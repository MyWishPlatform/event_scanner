package io.mywish.tron.blockchain;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.lastwill.eventscan.model.NetworkType;
import io.lastwill.eventscan.repositories.LastBlockRepository;
import io.mywish.scanner.services.LastBlockDbPersister;
import io.mywish.scanner.services.LastBlockFilePersister;
import io.mywish.scanner.services.LastBlockPersister;
import io.mywish.tron.blockchain.services.TronNetwork;
import io.mywish.tron.blockchain.services.TronScanner;
import io.mywish.troncli4j.service.TronClientImpl;
import org.apache.http.impl.client.CloseableHttpClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.net.URI;

@Configuration
@ComponentScan
public class TronBCModule {
    @ConditionalOnProperty("etherscanner.tron.rpc-url.testnet")
    @Bean(name = NetworkType.TRON_TESTNET_VALUE)
    public TronNetwork tronNetTest(
            final CloseableHttpClient closeableHttpClient,
            final ObjectMapper objectMapper,
            final @Value("${etherscanner.tron.rpc-url.testnet}") URI rpc
    ) throws Exception {
        return new TronNetwork(
                NetworkType.TRON_TESTNET,
                new TronClientImpl(
                        closeableHttpClient,
                        rpc,
                        objectMapper
                )
        );
    }

    @ConditionalOnProperty("etherscanner.tron.rpc-url.mainnet")
    @Bean(name = NetworkType.TRON_MAINNET_VALUE)
    public TronNetwork tronNetMain(
            final CloseableHttpClient closeableHttpClient,
            final ObjectMapper objectMapper,
            final @Value("${etherscanner.tron.rpc-url.mainnet}") URI rpc
    ) throws Exception {
        return new TronNetwork(
                NetworkType.TRON_MAINNET,
                new TronClientImpl(
                        closeableHttpClient,
                        rpc,
                        objectMapper
                )
        );
    }

    @ConditionalOnBean(name = NetworkType.TRON_MAINNET_VALUE)
    @Bean
    public TronScanner tronScannerMain(
            final @Qualifier(NetworkType.TRON_MAINNET_VALUE) TronNetwork network,
            final @Qualifier("tronMainnetLastBlockPersister") LastBlockPersister lastBlockPersister,
            final @Value("${etherscanner.tron.polling-interval-ms}") Long pollingInterval,
            final @Value("${etherscanner.tron.commit-chain-length}") Integer commitmentChainLength
    ) {
        return new TronScanner(
                network,
                lastBlockPersister,
                pollingInterval,
                commitmentChainLength
        );
    }

    @ConditionalOnBean(name = NetworkType.TRON_TESTNET_VALUE)
    @Bean
    public TronScanner tronScannerTest(
            final @Qualifier(NetworkType.TRON_TESTNET_VALUE) TronNetwork network,
            final @Qualifier("tronTestnetLastBlockPersister") LastBlockPersister lastBlockPersister,
            final @Value("${etherscanner.tron.polling-interval-ms}") Long pollingInterval,
            final @Value("${etherscanner.tron.commit-chain-length}") Integer commitmentChainLength
    ) {
        return new TronScanner(
                network,
                lastBlockPersister,
                pollingInterval,
                commitmentChainLength
        );
    }

    @Profile("tron-db-persister")
    @Configuration
    public static class DbPersisterConfiguration {
        @Bean
        public LastBlockPersister tronMainnetLastBlockPersister(
                LastBlockRepository lastBlockRepository,
                final @Value("${etherscanner.tron.last-block.mainnet:#{null}}") Long lastBlock
        ) {
            return new LastBlockDbPersister(NetworkType.TRON_MAINNET, lastBlockRepository, lastBlock);
        }

        @Bean
        public LastBlockPersister tronTestnetLastBlockPersister(
                LastBlockRepository lastBlockRepository,
                final @Value("${etherscanner.tron.last-block.testnet:#{null}}") Long lastBlock
        ) {
            return new LastBlockDbPersister(NetworkType.TRON_TESTNET, lastBlockRepository, lastBlock);
        }
    }

    @Profile("!tron-db-persister")
    @Configuration
    public static class FilePersisterConfiguration {
        @Bean
        public LastBlockPersister tronMainnetLastBlockPersister(
                final @Value("${etherscanner.start-block-dir}") String dir,
                final @Value("${etherscanner.tron.last-block.mainnet:#{null}}") Long lastBlock
        ) {
            return new LastBlockFilePersister(NetworkType.TRON_MAINNET, dir, lastBlock);
        }

        @Bean
        public LastBlockPersister tronTestnetLastBlockPersister(
                final @Value("${etherscanner.start-block-dir}") String dir,
                final @Value("${etherscanner.tron.last-block.testnet:#{null}}") Long lastBlock
        ) {
            return new LastBlockFilePersister(NetworkType.TRON_TESTNET, dir, lastBlock);
        }
    }

}
