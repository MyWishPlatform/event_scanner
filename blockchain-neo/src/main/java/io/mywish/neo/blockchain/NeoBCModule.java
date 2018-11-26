package io.mywish.neo.blockchain;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.lastwill.eventscan.model.NetworkType;
import io.lastwill.eventscan.repositories.LastBlockRepository;
import io.mywish.neo.blockchain.services.NeoNetwork;
import io.mywish.neo.blockchain.services.NeoScanner;
import io.mywish.neocli4j.NeoClientImpl;
import io.mywish.scanner.services.LastBlockDbPersister;
import io.mywish.scanner.services.LastBlockFilePersister;
import io.mywish.scanner.services.LastBlockPersister;
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
public class NeoBCModule {
    @ConditionalOnProperty(name = "etherscanner.neo.rpc-url.mainnet")
    @Bean(name = NetworkType.NEO_MAINNET_VALUE)
    public NeoNetwork neoNetMain(
            final CloseableHttpClient closeableHttpClient,
            final ObjectMapper objectMapper,
            final @Value("${etherscanner.neo.rpc-url.mainnet}") URI rpc
    ) {
        return new NeoNetwork(
                NetworkType.NEO_MAINNET,
                new NeoClientImpl(
                        closeableHttpClient,
                        rpc,
                        objectMapper
                )
        );
    }

    @ConditionalOnProperty(name = "etherscanner.neo.rpc-url.testnet")
    @Bean(name = NetworkType.NEO_TESTNET_VALUE)
    public NeoNetwork neoNetTest(
            final CloseableHttpClient closeableHttpClient,
            final ObjectMapper objectMapper,
            final @Value("${etherscanner.neo.rpc-url.testnet}") URI rpc
    ) {
        return new NeoNetwork(
                NetworkType.NEO_TESTNET,
                new NeoClientImpl(
                        closeableHttpClient,
                        rpc,
                        objectMapper
                )
        );
    }

    @Profile("neo-db-persister")
    @Configuration
    public class DbPersisterConfiguration {
        @Bean
        public LastBlockPersister neoMainnetLastBlockPersister(
                LastBlockRepository lastBlockRepository,
                final @Value("${etherscanner.neo.last-block.mainnet:#{null}}") Long lastBlock
        ) {
            return new LastBlockDbPersister(NetworkType.NEO_MAINNET, lastBlockRepository, lastBlock);
        }

        @Bean
        public LastBlockPersister neoTestnetLastBlockPersister(
                LastBlockRepository lastBlockRepository,
                final @Value("${etherscanner.neo.last-block.testnet:#{null}}") Long lastBlock
        ) {
            return new LastBlockDbPersister(NetworkType.NEO_TESTNET, lastBlockRepository, lastBlock);
        }
    }

    @Profile("!neo-db-persister")
    @Configuration
    public class FilePersisterConfiguration {
        @Bean
        public LastBlockPersister neoMainnetLastBlockPersister(
                final @Value("${etherscanner.start-block-dir}") String dir,
                final @Value("${etherscanner.neo.last-block.mainnet:#{null}}") Long lastBlock
        ) {
            return new LastBlockFilePersister(NetworkType.NEO_MAINNET, dir, lastBlock);
        }

        @Bean
        public LastBlockPersister neoTestnetLastBlockPersister(
                final @Value("${etherscanner.start-block-dir}") String dir,
                final @Value("${etherscanner.neo.last-block.testnet:#{null}}") Long lastBlock
        ) {
            return new LastBlockFilePersister(NetworkType.NEO_TESTNET, dir, lastBlock);
        }
    }

    @ConditionalOnBean(name = NetworkType.NEO_MAINNET_VALUE)
    @Bean
    public NeoScanner neoScannerMain(
            final NeoNetwork network,
            final @Qualifier("neoMainnetLastBlockPersister") LastBlockPersister lastBlockPersister,
            final @Value("${etherscanner.neo.polling-interval-ms}") Long pollingInterval,
            final @Value("${etherscanner.neo.commit-chain-length}") Integer commitmentChainLength
    ) {
        return new NeoScanner(
                network,
                lastBlockPersister,
                pollingInterval,
                commitmentChainLength
        );
    }

    @ConditionalOnBean(name = NetworkType.NEO_TESTNET_VALUE)
    @Bean
    public NeoScanner neoScannerTest(
            final NeoNetwork network,
            final @Qualifier("neoTestnetLastBlockPersister") LastBlockPersister lastBlockPersister,
            final @Value("${etherscanner.neo.polling-interval-ms}") Long pollingInterval,
            final @Value("${etherscanner.neo.commit-chain-length}") Integer commitmentChainLength
    ) {
        return new NeoScanner(
                network,
                lastBlockPersister,
                pollingInterval,
                commitmentChainLength
        );
    }
}
