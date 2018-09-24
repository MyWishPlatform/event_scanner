package io.mywish.neo.blockchain;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.lastwill.eventscan.model.NetworkType;
import io.mywish.neo.blockchain.services.NeoNetwork;
import io.mywish.neo.blockchain.services.NeoScanner;
import io.mywish.neocli4j.NeoClientImpl;
import io.mywish.scanner.services.LastBlockPersister;
import org.apache.http.impl.client.CloseableHttpClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

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
}
