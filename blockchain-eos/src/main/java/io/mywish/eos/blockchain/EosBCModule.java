package io.mywish.eos.blockchain;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.lastwill.eventscan.model.NetworkType;
import io.mywish.eos.blockchain.services.EosNetwork;
import io.mywish.eos.blockchain.services.EosScanner;
import io.mywish.eoscli4j.service.EosClientImpl;
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
public class EosBCModule {
    @ConditionalOnProperty(name = {"etherscanner.eos.rpc-url.testnet", "etherscanner.eos.tcp-url.testnet"})
    @Bean(name = NetworkType.EOS_TESTNET_VALUE)
    public EosNetwork eosNetTest(
            final CloseableHttpClient closeableHttpClient,
            final ObjectMapper objectMapper,
            final @Value("${etherscanner.eos.tcp-url.testnet}") URI rcpUrl,
            final @Value("${etherscanner.eos.rpc-url.testnet}") URI rpc
    ) throws Exception {
        return new EosNetwork(
                NetworkType.EOS_TESTNET,
                new EosClientImpl(
                        rcpUrl,
                        closeableHttpClient,
                        rpc,
                        objectMapper
                )
        );
    }

    @ConditionalOnProperty(name = {"etherscanner.eos.rpc-url.mainnet", "etherscanner.eos.tcp-url.mainnet"})
    @Bean(name = NetworkType.EOS_MAINNET_VALUE)
    public EosNetwork eosNetMain(
            final CloseableHttpClient closeableHttpClient,
            final ObjectMapper objectMapper,
            final @Value("${etherscanner.eos.tcp-url.mainnet}") URI tcpUrl,
            final @Value("${etherscanner.eos.rpc-url.mainnet}") URI rpc
    ) throws Exception {
        return new EosNetwork(
                NetworkType.EOS_MAINNET,
                new EosClientImpl(
                        tcpUrl,
                        closeableHttpClient,
                        rpc,
                        objectMapper
                )
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

}
