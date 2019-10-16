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
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.web3j.protocol.websocket.WebSocketClient;

import java.net.ConnectException;
import java.net.URI;

@Configuration
@ComponentScan
public class Web3BCModule {
    @ConditionalOnProperty(name = "io.lastwill.eventscan.web3-url.ethereum")
    @Bean(name = NetworkType.ETHEREUM_MAINNET_VALUE)
    public Web3Network ethNetMain(
            @Value("${io.lastwill.eventscan.web3-url.ethereum}") URI web3Url,
            @Value("${etherscanner.polling-interval-ms:5000}") Long pollingInterval,
            @Value("${etherscanner.pending-transactions-threshold}") int pendingThreshold) throws ConnectException {
        return new Web3Network(
                NetworkType.ETHEREUM_MAINNET,
                new WebSocketClient(web3Url),
                pollingInterval,
                pendingThreshold);
    }

    @ConditionalOnProperty(name = "io.lastwill.eventscan.web3-url.ropsten")
    @Bean(name = NetworkType.ETHEREUM_ROPSTEN_VALUE)
    public Web3Network ethNetRopsten(
            @Value("${io.lastwill.eventscan.web3-url.ropsten}") URI web3Url,
            @Value("${etherscanner.polling-interval-ms:5000}") Long pollingInterval,
            @Value("${etherscanner.pending-transactions-threshold}") int pendingThreshold) throws ConnectException {
        return new Web3Network(
                NetworkType.ETHEREUM_ROPSTEN,
                new WebSocketClient(web3Url),
                pollingInterval,
                pendingThreshold);
    }
/*
    @ConditionalOnProperty(name = "io.lastwill.eventscan.web3-url.rsk-mainnet")
    @Bean(name = NetworkType.RSK_MAINNET_VALUE)
    public Web3Network rskNetMain(@Value("${io.lastwill.eventscan.web3-url.rsk-mainnet}") String web3Url) throws ConnectException {
        return new Web3Network(
                NetworkType.RSK_MAINNET,
                new HttpService(web3Url),
                0);
    }

    @ConditionalOnProperty(name = "io.lastwill.eventscan.web3-url.rsk-testnet")
    @Bean(name = NetworkType.RSK_TESTNET_VALUE)
    public Web3Network rskNetTest(@Value("${io.lastwill.eventscan.web3-url.rsk-testnet}") String web3Url) throws ConnectException {
        return new Web3Network(
                NetworkType.RSK_TESTNET,
                new HttpService(web3Url),
                0);
    }
*/
    @Configuration
    @ConditionalOnProperty("etherscanner.ethereum.db-persister")
    public class EthDbPersisterConfiguration {
        @Bean
        public LastBlockPersister ethMainnetLastBlockPersister(
                LastBlockRepository lastBlockRepository
        ) {
            return new LastBlockDbPersister(NetworkType.ETHEREUM_MAINNET, lastBlockRepository, null);
        }

        @Bean
        public LastBlockPersister ethRopstenLastBlockPersister(
                LastBlockRepository lastBlockRepository
        ) {
            return new LastBlockDbPersister(NetworkType.ETHEREUM_ROPSTEN, lastBlockRepository, null);
        }
    }

    @Configuration
    @ConditionalOnProperty(value = "etherscanner.ethereum.db-persister", havingValue = "false", matchIfMissing = true)
    public class EthFilePersisterConfiguration {
        @Bean
        public LastBlockPersister ethMainnetLastBlockPersister(
                final @Value("${etherscanner.start-block-dir}") String dir
        ) {
            return new LastBlockFilePersister(NetworkType.ETHEREUM_MAINNET, dir, null);
        }

        @Bean
        public LastBlockPersister ethRopstenLastBlockPersister(
                final @Value("${etherscanner.start-block-dir}") String dir
        ) {
            return new LastBlockFilePersister(NetworkType.ETHEREUM_ROPSTEN, dir, null);
        }
    }
/*
    @Configuration
    @ConditionalOnProperty("etherscanner.rsk.db-persister")
    public class RskDbPersisterConfiguration {
        @Bean
        public LastBlockPersister rskMainnetLastBlockPersister(
                LastBlockRepository lastBlockRepository
        ) {
            return new LastBlockDbPersister(NetworkType.RSK_MAINNET, lastBlockRepository, null);
        }

        @Bean
        public LastBlockPersister rskTestnetLastBlockPersister(
                LastBlockRepository lastBlockRepository
        ) {
            return new LastBlockDbPersister(NetworkType.RSK_TESTNET, lastBlockRepository, null);
        }
    }

    @Configuration
    @ConditionalOnProperty(value = "etherscanner.rsk.db-persister", havingValue = "false", matchIfMissing = true)
    public class RskFilePersisterConfiguration {
        @Bean
        public LastBlockPersister rskMainnetLastBlockPersister(
                final @Value("${etherscanner.start-block-dir}") String dir
        ) {
            return new LastBlockFilePersister(NetworkType.RSK_MAINNET, dir, null);
        }

        @Bean
        public LastBlockPersister rskTestnetLastBlockPersister(
                final @Value("${etherscanner.start-block-dir}") String dir
        ) {
            return new LastBlockFilePersister(NetworkType.RSK_TESTNET, dir, null);
        }
    }
*/
    @ConditionalOnBean(name = NetworkType.ETHEREUM_MAINNET_VALUE)
    @Bean
    public Web3Scanner ethScannerMain(
            final @Qualifier(NetworkType.ETHEREUM_MAINNET_VALUE) Web3Network network,
            final @Qualifier("ethMainnetLastBlockPersister") LastBlockPersister lastBlockPersister,
            final @Value("${etherscanner.polling-interval-ms:5000}") Long pollingInterval,
            final @Value("${etherscanner.commit-chain-length:5}") Integer commitmentChainLength
    ) {
        return new Web3Scanner(
                network,
                lastBlockPersister,
                pollingInterval,
                commitmentChainLength
        );
    }

    @ConditionalOnBean(name = NetworkType.ETHEREUM_ROPSTEN_VALUE)
    @Bean
    public Web3Scanner ethScannerRopsten(
            final @Qualifier(NetworkType.ETHEREUM_ROPSTEN_VALUE) Web3Network network,
            final @Qualifier("ethRopstenLastBlockPersister") LastBlockPersister lastBlockPersister,
            final @Value("${etherscanner.polling-interval-ms:5000}") Long pollingInterval,
            final @Value("${etherscanner.commit-chain-length:5}") Integer commitmentChainLength
    ) {
        return new Web3Scanner(
                network,
                lastBlockPersister,
                pollingInterval,
                commitmentChainLength
        );
    }
/*
    @ConditionalOnBean(name = NetworkType.RSK_MAINNET_VALUE)
    @Bean
    public Web3Scanner rskScannerMain(
            final @Qualifier(NetworkType.RSK_MAINNET_VALUE) Web3Network network,
            final @Qualifier("rskMainnetLastBlockPersister") LastBlockPersister lastBlockPersister,
            final @Value("${etherscanner.polling-interval-ms:5000}") Long pollingInterval,
            final @Value("${etherscanner.commit-chain-length:5}") Integer commitmentChainLength
    ) {
        return new Web3Scanner(
                network,
                lastBlockPersister,
                pollingInterval,
                commitmentChainLength
        );
    }

    @ConditionalOnBean(name = NetworkType.RSK_TESTNET_VALUE)
    @Bean
    public Web3Scanner rskScannerTest(
            final @Qualifier(NetworkType.RSK_TESTNET_VALUE) Web3Network network,
            final @Qualifier("rskTestnetLastBlockPersister") LastBlockPersister lastBlockPersister,
            final @Value("${etherscanner.polling-interval-ms:5000}") Long pollingInterval,
            final @Value("${etherscanner.commit-chain-length:5}") Integer commitmentChainLength
    ) {
        return new Web3Scanner(
                network,
                lastBlockPersister,
                pollingInterval,
                commitmentChainLength
        );
    }
 */
}
