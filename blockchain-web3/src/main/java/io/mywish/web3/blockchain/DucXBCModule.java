package io.mywish.web3.blockchain;

import io.lastwill.eventscan.model.NetworkType;
import io.lastwill.eventscan.repositories.LastBlockRepository;
import io.mywish.scanner.services.LastBlockDbPersister;
import io.mywish.scanner.services.LastBlockFilePersister;
import io.mywish.scanner.services.LastBlockPersister;
import io.mywish.web3.blockchain.service.Web3Network;
import io.mywish.web3.blockchain.service.Web3Scanner;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.web3j.protocol.websocket.WebSocketClient;

import java.net.ConnectException;
import java.net.URI;

@Component
public class DucXBCModule {
    @ConditionalOnProperty(name = "io.lastwill.eventscan.ducatusx.mainnet")
    @Bean(name = NetworkType.DUCX_MAINNET_VALUE)
    public Web3Network ducXNetMain(
            @Value("${io.lastwill.eventscan.ducatusx.mainnet}") URI web3Url,
            @Value("${etherscanner.ducatusx.polling-interval-ms:30000}") Long pollingInterval,
            @Value("${etherscanner.pending-transactions-threshold}") int pendingThreshold) throws ConnectException {
        return new Web3Network(
                NetworkType.DUCX_MAINNET,
                new WebSocketClient(web3Url),
                pollingInterval,
                pendingThreshold);
    }

    @ConditionalOnProperty(name = "io.lastwill.eventscan.ducatusx.testnet")
    @Bean(name = NetworkType.DUCX_TESTNET_VALUE)
    public Web3Network ducXNetTest(
            @Value("${io.lastwill.eventscan.ducatusx.testnet}") URI web3Url,
            @Value("${etherscanner.ducatusx.polling-interval-ms:30000}") Long pollingInterval,
            @Value("${etherscanner.pending-transactions-threshold}") int pendingThreshold) throws ConnectException {
        return new Web3Network(
                NetworkType.DUCX_TESTNET,
                new WebSocketClient(web3Url),
                pollingInterval,
                pendingThreshold);
    }

    @Configuration
    @ConditionalOnProperty("etherscanner.ducatusx.db-persister")
    public class DucXDbPersisterConfiguration {
        @Bean
        public LastBlockPersister ducXMainnetLastBlockPersister(
                LastBlockRepository lastBlockRepository
        ) {
            return new LastBlockDbPersister(NetworkType.DUCX_MAINNET, lastBlockRepository, null);
        }

        @Bean
        public LastBlockPersister ducXTestnetLastBlockPersister(
                LastBlockRepository lastBlockRepository
        ) {
            return new LastBlockDbPersister(NetworkType.DUCX_TESTNET, lastBlockRepository, null);
        }
    }

    @Configuration
    @ConditionalOnProperty(value = "etherscanner.ducatusx.db-persister", havingValue = "false", matchIfMissing = true)
    public class DucXFilePersisterConfiguration {
        @Bean
        public LastBlockPersister ducXMainnetLastBlockPersister(
                final @Value("${etherscanner.start-block-dir}") String dir
        ) {
            return new LastBlockFilePersister(NetworkType.DUCX_MAINNET, dir, null);
        }

        @Bean
        public LastBlockPersister ducXTestnetLastBlockPersister(
                final @Value("${etherscanner.start-block-dir}") String dir
        ) {
            return new LastBlockFilePersister(NetworkType.DUCX_TESTNET, dir, null);
        }
    }

    @ConditionalOnBean(name = NetworkType.DUCX_MAINNET_VALUE)
    @Bean
    public Web3Scanner ducXScannerMain(
            final @Qualifier(NetworkType.DUCX_MAINNET_VALUE) Web3Network network,
            final @Qualifier("ducXMainnetLastBlockPersister") LastBlockPersister lastBlockPersister,
            final @Value("${etherscanner.ducatusx.polling-interval-ms:30000}") Long pollingInterval,
            final @Value("${etherscanner.commit-chain-length:5}") Integer commitmentChainLength
    ) {
        return new Web3Scanner(
                network,
                lastBlockPersister,
                pollingInterval,
                commitmentChainLength
        );
    }

    @ConditionalOnBean(name = NetworkType.DUCX_TESTNET_VALUE)
    @Bean
    public Web3Scanner ducXScannerRopsten(
            final @Qualifier(NetworkType.DUCX_TESTNET_VALUE) Web3Network network,
            final @Qualifier("ducXTestnetLastBlockPersister") LastBlockPersister lastBlockPersister,
            final @Value("${etherscanner.ducatusx.polling-interval-ms:30000}") Long pollingInterval,
            final @Value("${etherscanner.commit-chain-length:5}") Integer commitmentChainLength
    ) {
        return new Web3Scanner(
                network,
                lastBlockPersister,
                pollingInterval,
                commitmentChainLength
        );
    }
}