package io.mywish.web3.blockchain;

import io.lastwill.eventscan.model.NetworkType;
import io.lastwill.eventscan.repositories.LastBlockRepository;
import io.mywish.scanner.services.LastBlockDbPersister;
import io.mywish.scanner.services.LastBlockFilePersister;
import io.mywish.scanner.services.LastBlockPersister;
import io.mywish.web3.blockchain.parity.Web3jEx;
import io.mywish.web3.blockchain.service.Web3Network;
import io.mywish.web3.blockchain.service.Web3Scanner;
import okhttp3.OkHttpClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;

@Configuration
@ComponentScan
public class Web3BCModule {
    @ConditionalOnProperty(name = "io.lastwill.eventscan.web3-url.ethereum")
    @Bean(name = NetworkType.ETHEREUM_MAINNET_VALUE)
    public Web3Network ethNetMain(
            OkHttpClient client,
            @Value("${io.lastwill.eventscan.web3-url.ethereum}") String web3Url,
            @Value("${etherscanner.pending-transactions-threshold}") int pendingThreshold) {
        return new Web3Network(
                NetworkType.ETHEREUM_MAINNET,
                Web3jEx.build(new HttpService(web3Url, client, false)),
                pendingThreshold);
    }

    @ConditionalOnProperty(name = "io.lastwill.eventscan.web3-url.ropsten")
    @Bean(name = NetworkType.ETHEREUM_ROPSTEN_VALUE)
    public Web3Network ethNetRopsten(
            OkHttpClient client,
            @Value("${io.lastwill.eventscan.web3-url.ropsten}") String web3Url,
            @Value("${etherscanner.pending-transactions-threshold}") int pendingThreshold) {
        return new Web3Network(
                NetworkType.ETHEREUM_ROPSTEN,
                Web3jEx.build(new HttpService(web3Url, client, false)),
                pendingThreshold);
    }

    @ConditionalOnProperty(name = "io.lastwill.eventscan.web3-url.rsk-mainnet")
    @Bean(name = NetworkType.RSK_MAINNET_VALUE)
    public Web3Network rskNetMain(@Value("${io.lastwill.eventscan.web3-url.rsk-mainnet}") String web3Url) {
        return new Web3Network(
                NetworkType.RSK_MAINNET,
                Web3j.build(new HttpService(web3Url)),
                0);
    }

    @ConditionalOnProperty(name = "io.lastwill.eventscan.web3-url.rsk-testnet")
    @Bean(name = NetworkType.RSK_TESTNET_VALUE)
    public Web3Network rskNetTest(@Value("${io.lastwill.eventscan.web3-url.rsk-testnet}") String web3Url) {
        return new Web3Network(
                NetworkType.RSK_TESTNET,
                Web3j.build(new HttpService(web3Url)),
                0);
    }

    @ConditionalOnProperty(value = "etherscanner.eth.db-block-persister", havingValue = "true")
    @ConditionalOnBean(name = NetworkType.ETHEREUM_MAINNET_VALUE)
    @Bean("mainnetEthLastBlockPersister")
    public LastBlockPersister ethMainnetLastBlockPersister(
            final @Qualifier(NetworkType.ETHEREUM_MAINNET_VALUE) Web3Network network,
            final LastBlockRepository lastBlockRepository
    ) {
        return new LastBlockDbPersister(network.getType(), lastBlockRepository, null);
    }

    @ConditionalOnProperty(value = "etherscanner.eth.db-block-persister", havingValue = "false", matchIfMissing = true)
    @ConditionalOnBean(name = NetworkType.ETHEREUM_MAINNET_VALUE)
    @Bean("mainnetEthLastBlockPersister")
    public LastBlockPersister ethMainnetLastBlockPersister(
            final @Qualifier(NetworkType.ETHEREUM_MAINNET_VALUE) Web3Network network,
            final @Value("${etherscanner.start-block-dir}") String dir
    ) {
        return new LastBlockFilePersister(network.getType(), dir, null);
    }

    @ConditionalOnProperty(value = "etherscanner.eth.db-block-persister", havingValue = "true")
    @ConditionalOnBean(name = NetworkType.ETHEREUM_ROPSTEN_VALUE)
    @Bean("ropstenEthLastBlockPersister")
    public LastBlockPersister ethRopstenLastBlockPersister(
            final @Qualifier(NetworkType.ETHEREUM_ROPSTEN_VALUE) Web3Network network,
            final LastBlockRepository lastBlockRepository
    ) {
        return new LastBlockDbPersister(network.getType(), lastBlockRepository, null);
    }

    @ConditionalOnProperty(value = "etherscanner.eth.db-block-persister", havingValue = "false", matchIfMissing = true)
    @ConditionalOnBean(name = NetworkType.ETHEREUM_ROPSTEN_VALUE)
    @Bean("ropstenEthLastBlockPersister")
    public LastBlockPersister ethRopstenLastBlockPersister(
            final @Qualifier(NetworkType.ETHEREUM_ROPSTEN_VALUE) Web3Network network,
            final @Value("${etherscanner.start-block-dir}") String dir
    ) {
        return new LastBlockFilePersister(network.getType(), dir, null);
    }

    @ConditionalOnProperty(value = "etherscanner.rsk.db-block-persister", havingValue = "true")
    @ConditionalOnBean(name = NetworkType.RSK_MAINNET_VALUE)
    @Bean("mainnetRskLastBlockPersister")
    public LastBlockPersister rskMainnetLastBlockPersister(
            final @Qualifier(NetworkType.RSK_MAINNET_VALUE) Web3Network network,
            final LastBlockRepository lastBlockRepository
    ) {
        return new LastBlockDbPersister(network.getType(), lastBlockRepository, null);
    }

    @ConditionalOnProperty(value = "etherscanner.rsk.db-block-persister", havingValue = "false", matchIfMissing = true)
    @ConditionalOnBean(name = NetworkType.RSK_MAINNET_VALUE)
    @Bean("mainnetRskLastBlockPersister")
    public LastBlockPersister rskMainnetLastBlockPersister(
            final @Qualifier(NetworkType.RSK_MAINNET_VALUE) Web3Network network,
            final @Value("${etherscanner.start-block-dir}") String dir
    ) {
        return new LastBlockFilePersister(network.getType(), dir, null);
    }

    @ConditionalOnProperty(value = "etherscanner.rsk.db-block-persister", havingValue = "true")
    @ConditionalOnBean(name = NetworkType.RSK_TESTNET_VALUE)
    @Bean("testnetRskLastBlockPersister")
    public LastBlockPersister rskTestnetLastBlockPersister(
            final @Qualifier(NetworkType.RSK_TESTNET_VALUE) Web3Network network,
            final LastBlockRepository lastBlockRepository
    ) {
        return new LastBlockDbPersister(network.getType(), lastBlockRepository, null);
    }

    @ConditionalOnProperty(value = "etherscanner.rsk.db-block-persister", havingValue = "false", matchIfMissing = true)
    @ConditionalOnBean(name = NetworkType.RSK_TESTNET_VALUE)
    @Bean("testnetRskLastBlockPersister")
    public LastBlockPersister rskTestnetLastBlockPersister(
            final @Qualifier(NetworkType.RSK_TESTNET_VALUE) Web3Network network,
            final @Value("${etherscanner.start-block-dir}") String dir
    ) {
        return new LastBlockFilePersister(network.getType(), dir, null);
    }

    @ConditionalOnBean(name = NetworkType.ETHEREUM_MAINNET_VALUE)
    @Bean
    public Web3Scanner ethScannerMain(
            final @Qualifier(NetworkType.ETHEREUM_MAINNET_VALUE) Web3Network network,
            final @Qualifier("mainnetEthLastBlockPersister") LastBlockPersister lastBlockPersister,
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
            final @Qualifier("ropstenEthLastBlockPersister") LastBlockPersister lastBlockPersister,
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

    @ConditionalOnBean(name = NetworkType.RSK_MAINNET_VALUE)
    @Bean
    public Web3Scanner rskScannerMain(
            final @Qualifier(NetworkType.RSK_MAINNET_VALUE) Web3Network network,
            final @Qualifier("mainnetRskLastBlockPersister") LastBlockPersister lastBlockPersister,
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
            final @Qualifier("testnetRskLastBlockPersister") LastBlockPersister lastBlockPersister,
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
}
