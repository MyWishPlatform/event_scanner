package io.mywish.binance.blockchain;

import com.binance.dex.api.client.BinanceDexApiClientFactory;
import io.lastwill.eventscan.model.NetworkType;
import io.lastwill.eventscan.repositories.LastBlockRepository;
import io.mywish.binance.blockchain.services.BinanceNetwork;
import io.mywish.binance.blockchain.services.BinanceScanner;
import io.mywish.scanner.services.LastBlockDbPersister;
import io.mywish.scanner.services.LastBlockFilePersister;
import io.mywish.scanner.services.LastBlockPersister;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan
public class BinanceBCModule {
//    @ConditionalOnProperty({"etherscanner.binance.rpc-url.testnet", "etherscanner.binance.hrp.testnet"})
//    @Bean(name = NetworkType.BINANCE_TESTNET_VALUE)
//    public BinanceNetwork binanceNetTest(
//            final @Value("${etherscanner.binance.rpc-url.testnet}") String rpcUrl,
//            final @Value("${etherscanner.binance.hrp.testnet}") String hrp
//    ) {
//        return new BinanceNetwork(
//                NetworkType.BINANCE_TESTNET,
//                BinanceDexApiClientFactory.newInstance().newNodeRpcClient(rpcUrl, hrp)
//        );
//    }

    @ConditionalOnProperty({"etherscanner.binance.rpc-url.mainnet", "etherscanner.binance.hrp.mainnet"})
    @Bean(name = NetworkType.BINANCE_MAINNET_VALUE)
    public BinanceNetwork binanceNetMain(
            final @Value("${etherscanner.binance.rpc-url.mainnet}") String rpcUrl,
            final @Value("${etherscanner.binance.hrp.mainnet}") String hrp
    ) {
        return new BinanceNetwork(
                NetworkType.BINANCE_MAINNET,
                BinanceDexApiClientFactory.newInstance().newNodeRpcClient(rpcUrl, hrp)
        );
    }

    @ConditionalOnBean(name = NetworkType.BINANCE_MAINNET_VALUE)
    @Bean
    public BinanceScanner binanceScannerMain(
            final @Qualifier(NetworkType.BINANCE_MAINNET_VALUE) BinanceNetwork network,
            final @Qualifier("binanceMainnetLastBlockPersister") LastBlockPersister lastBlockPersister,
            final @Value("${etherscanner.binance.polling-interval-ms}") Long pollingInterval,
            final @Value("${etherscanner.binance.commit-chain-length}") Integer commitmentChainLength
    ) {
        return new BinanceScanner(
                network,
                lastBlockPersister,
                pollingInterval,
                commitmentChainLength
        );
    }

//    @ConditionalOnBean(name = NetworkType.BINANCE_TESTNET_VALUE)
//    @Bean
//    public BinanceScanner binanceScannerTest(
//            final @Qualifier(NetworkType.BINANCE_TESTNET_VALUE) BinanceNetwork network,
//            final @Qualifier("binanceTestnetLastBlockPersister") LastBlockPersister lastBlockPersister,
//            final @Value("${etherscanner.binance.polling-interval-ms}") Long pollingInterval,
//            final @Value("${etherscanner.binance.commit-chain-length}") Integer commitmentChainLength
//    ) {
//        return new BinanceScanner(
//                network,
//                lastBlockPersister,
//                pollingInterval,
//                commitmentChainLength
//        );
//    }

    @Configuration
    @ConditionalOnProperty("etherscanner.binance.db-persister")
    public static class DbPersisterConfiguration {
        @Bean
        public LastBlockPersister binanceMainnetLastBlockPersister(
                LastBlockRepository lastBlockRepository,
                final @Value("${etherscanner.binance.last-block.mainnet:#{null}}") Long lastBlock
        ) {
            return new LastBlockDbPersister(NetworkType.BINANCE_MAINNET, lastBlockRepository, lastBlock);
        }

//        @Bean
//        public LastBlockPersister binanceTestnetLastBlockPersister(
//                LastBlockRepository lastBlockRepository,
//                final @Value("${etherscanner.binance.last-block.testnet:#{null}}") Long lastBlock
//        ) {
//            return new LastBlockDbPersister(NetworkType.BINANCE_TESTNET, lastBlockRepository, lastBlock);
//        }
    }

    @Configuration
    @ConditionalOnProperty(value = "etherscanner.binance.db-persister", havingValue = "false", matchIfMissing = true)
    public static class FilePersisterConfiguration {
        @Bean
        public LastBlockPersister binanceMainnetLastBlockPersister(
                final @Value("${etherscanner.start-block-dir}") String dir,
                final @Value("${etherscanner.binance.last-block.mainnet:#{null}}") Long lastBlock
        ) {
            return new LastBlockFilePersister(NetworkType.BINANCE_MAINNET, dir, lastBlock);
        }

//        @Bean
//        public LastBlockPersister binanceTestnetLastBlockPersister(
//                final @Value("${etherscanner.start-block-dir}") String dir,
//                final @Value("${etherscanner.binance.last-block.testnet:#{null}}") Long lastBlock
//        ) {
//            return new LastBlockFilePersister(NetworkType.BINANCE_TESTNET, dir, lastBlock);
//        }
    }

}
