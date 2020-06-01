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
public class BinanceSmartChainBCModule {
    @ConditionalOnProperty(name = "io.lastwill.eventscan.binance-smart.mainnet")
    @Bean(name = NetworkType.BINANCE_SMART_MAINNET_VALUE)
    public Web3Network binanceSmartNetMain(
            @Value("${io.lastwill.eventscan.binance-smart.mainnet}") URI web3Url,
            @Value("${etherscanner.binance-smart.polling-interval-ms:30000}") Long pollingInterval,
            @Value("${etherscanner.binance-smart.pending-transactions-threshold}") int pendingThreshold) throws ConnectException {
        return new Web3Network(
                NetworkType.BINANCE_SMART_MAINNET,
                new WebSocketClient(web3Url),
                pollingInterval,
                pendingThreshold);
    }

    @ConditionalOnProperty(name = "io.lastwill.eventscan.binance-smart.testnet")
    @Bean(name = NetworkType.BINANCE_SMART_TESTNET_VALUE)
    public Web3Network binanceSmartNetTest(
            @Value("${io.lastwill.eventscan.binance-smart.testnet}") URI web3Url,
            @Value("${etherscanner.binance-smart.polling-interval-ms:30000}") Long pollingInterval,
            @Value("${etherscanner.binance-smart.pending-transactions-threshold}") int pendingThreshold) throws ConnectException {
        return new Web3Network(
                NetworkType.BINANCE_SMART_TESTNET,
                new WebSocketClient(web3Url),
                pollingInterval,
                pendingThreshold);
    }

    @Configuration
    @ConditionalOnProperty("etherscanner.binance-smart.db-persister")
    public static class BinanceSmartDbPersisterConfiguration {
        @Bean
        public LastBlockPersister binanceSmartMainnetLastBlockPersister(
                LastBlockRepository lastBlockRepository
        ) {
            return new LastBlockDbPersister(NetworkType.BINANCE_SMART_MAINNET, lastBlockRepository, null);
        }

        @Bean
        public LastBlockPersister binanceSmartTestnetLastBlockPersister(
                LastBlockRepository lastBlockRepository
        ) {
            return new LastBlockDbPersister(NetworkType.BINANCE_SMART_TESTNET, lastBlockRepository, null);
        }
    }

    @Configuration
    @ConditionalOnProperty(value = "etherscanner.binance-smart.db-persister", havingValue = "false", matchIfMissing = true)
    public static class BinanceSmartFilePersisterConfiguration {
        @Bean
        public LastBlockPersister binanceSmartMainnetLastBlockPersister(
                final @Value("${etherscanner.start-block-dir}") String dir
        ) {
            return new LastBlockFilePersister(NetworkType.BINANCE_SMART_MAINNET, dir, null);
        }

        @Bean
        public LastBlockPersister binanceSmartTestnetLastBlockPersister(
                final @Value("${etherscanner.start-block-dir}") String dir
        ) {
            return new LastBlockFilePersister(NetworkType.BINANCE_SMART_TESTNET, dir, null);
        }
    }

    @ConditionalOnBean(name = NetworkType.BINANCE_SMART_MAINNET_VALUE)
    @Bean
    public Web3Scanner binanceSmartScannerMain(
            final @Qualifier(NetworkType.BINANCE_SMART_MAINNET_VALUE) Web3Network network,
            final @Qualifier("binanceSmartMainnetLastBlockPersister") LastBlockPersister lastBlockPersister,
            final @Value("${etherscanner.binancesc.polling-interval-ms:30000}") Long pollingInterval,
            final @Value("${etherscanner.commit-chain-length:5}") Integer commitmentChainLength
    ) {
        return new Web3Scanner(
                network,
                lastBlockPersister,
                pollingInterval,
                commitmentChainLength
        );
    }

    @ConditionalOnBean(name = NetworkType.BINANCE_SMART_TESTNET_VALUE)
    @Bean
    public Web3Scanner binanceSmartScannerRopsten(
            final @Qualifier(NetworkType.BINANCE_SMART_TESTNET_VALUE) Web3Network network,
            final @Qualifier("binanceSmartTestnetLastBlockPersister") LastBlockPersister lastBlockPersister,
            final @Value("${etherscanner.binancesc.polling-interval-ms:30000}") Long pollingInterval,
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
