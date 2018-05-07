package io.mywish.scanner;

import com.glowstick.neocli4j.NeoClient;
import com.glowstick.neocli4j.NeoClientImpl;
import com.neemre.btcdcli4j.core.client.BtcdClient;
import com.neemre.btcdcli4j.core.client.BtcdClientImpl;
import io.mywish.scanner.model.NetworkType;
import io.mywish.scanner.services.BtcScanner;
import io.mywish.scanner.services.LastBlockPersister;
import io.mywish.scanner.services.NeoScanner;
import io.mywish.scanner.services.Web3Scanner;
import org.apache.http.impl.client.CloseableHttpClient;
import org.bitcoinj.params.MainNetParams;
import org.bitcoinj.params.TestNet3Params;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.*;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;
import java.net.URI;

@Configuration
@ComponentScan
@PropertySource("classpath:scanner.properties")
public class ScannerModule {
    @Bean
    public static PropertySourcesPlaceholderConfigurer propertyConfig() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    @ConditionalOnProperty(name = "io.lastwill.eventscan.web3-url.ethereum")
    @Bean(name = NetworkType.ETHEREUM_MAINNET_VALUE)
    public Web3j web3jEthereum(@Value("${io.lastwill.eventscan.web3-url.ethereum}") String web3Url) {
        return Web3j.build(new HttpService(web3Url));
    }

    @ConditionalOnProperty(name = "io.lastwill.eventscan.web3-url.ropsten")
    @Bean(name = NetworkType.ETHEREUM_ROPSTEN_VALUE)
    public Web3j web3jRopsten(@Value("${io.lastwill.eventscan.web3-url.ropsten}") String web3Url) {
        return Web3j.build(new HttpService(web3Url));
    }

    @ConditionalOnProperty(name = "io.lastwill.eventscan.web3-url.rsk-mainnet")
    @Bean(name = NetworkType.RSK_MAINNET_VALUE)
    public Web3j web3jRsk(@Value("${io.lastwill.eventscan.web3-url.rsk-mainnet}") String web3Url) {
        return Web3j.build(new HttpService(web3Url));
    }

    @ConditionalOnProperty(name = "io.lastwill.eventscan.web3-url.rsk-testnet")
    @Bean(name = NetworkType.RSK_TESTNET_VALUE)
    public Web3j web3jRskTest(@Value("${io.lastwill.eventscan.web3-url.rsk-testnet}") String web3Url) {
        return Web3j.build(new HttpService(web3Url));
    }

    @ConditionalOnProperty("etherscanner.bitcoin.rpc-url.mainnet")
    @Bean
    public BtcScanner btcScanner(
            final CloseableHttpClient closeableHttpClient,
            final @Value("${etherscanner.bitcoin.rpc-url.mainnet}") URI rpc,
            final @Value("${etherscanner.start-block-dir}") String dir,
            final @Value("${etherscanner.bitcoin.last-block.mainnet:#{null}}") Long lastBlock
    ) throws Exception {
        String user = null, password = null;
        if (rpc.getUserInfo() != null) {
            String[] credentials = rpc.getUserInfo().split(":");
            if (credentials.length > 1) {
                user = credentials[0];
                password = credentials[1];
            }
        }
        BtcdClient client = new BtcdClientImpl(
                closeableHttpClient,
                rpc.getScheme(),
                rpc.getHost(),
                rpc.getPort(),
                user,
                password
        );
        LastBlockPersister lastBlockPersister = new LastBlockPersister(NetworkType.BTC_MAINNET, dir, lastBlock);
        return new BtcScanner(client, NetworkType.BTC_MAINNET, lastBlockPersister, new MainNetParams());
    }

    @ConditionalOnProperty("etherscanner.bitcoin.rpc-url.testnet")
    @Bean
    public BtcScanner btcScannerTestnet(
            final CloseableHttpClient closeableHttpClient,
            final @Value("${etherscanner.bitcoin.rpc-url.testnet}") URI rpc,
            final @Value("${etherscanner.start-block-dir}") String dir,
            final @Value("${etherscanner.bitcoin.last-block.testnet:#{null}}") Long lastBlock
    ) throws Exception {
        String user = null, password = null;
        if (rpc.getUserInfo() != null) {
            String[] credentials = rpc.getUserInfo().split(":");
            if (credentials.length > 1) {
                user = credentials[0];
                password = credentials[1];
            }
        }
        BtcdClient client = new BtcdClientImpl(
                closeableHttpClient,
                rpc.getScheme(),
                rpc.getHost(),
                rpc.getPort(),
                user,
                password
        );
        LastBlockPersister lastBlockPersister = new LastBlockPersister(NetworkType.BTC_TESTNET_3, dir, lastBlock);
        return new BtcScanner(client, NetworkType.BTC_TESTNET_3, lastBlockPersister, new TestNet3Params());
    }

    @ConditionalOnProperty("etherscanner.neo.rpc-url.mainnet")
    @Bean
    public NeoScanner neoScanner(
            final CloseableHttpClient closeableHttpClient,
            final @Value("${etherscanner.neo.rpc-url.mainnet}") URI rpc,
            final @Value("${etherscanner.start-block-dir}") String dir,
            final @Value("${etherscanner.neo.last-block.mainnet:#{null}}") Long lastBlock
    ) {
        NeoClient client = new NeoClientImpl(
                closeableHttpClient,
                rpc
        );
        LastBlockPersister lastBlockPersister = new LastBlockPersister(NetworkType.NEO_MAINNET, dir, lastBlock);
        return new NeoScanner(client, NetworkType.NEO_MAINNET, lastBlockPersister);
    }

    @ConditionalOnBean(name = NetworkType.ETHEREUM_MAINNET_VALUE)
    @Bean
    public Web3Scanner ethScanner(
            final @Qualifier(NetworkType.ETHEREUM_MAINNET_VALUE) Web3j web3j,
            final @Value("${etherscanner.start-block-dir}") String dir,
            final @Value("${etherscanner.polling-interval-ms:5000}") Long pollingInterval,
            final @Value("${etherscanner.commit-chain-length:5}") Integer commitmentChainLength
    ) {
        return new Web3Scanner(
                NetworkType.ETHEREUM_MAINNET,
                web3j,
                new LastBlockPersister(NetworkType.ETHEREUM_MAINNET, dir, null),
                pollingInterval,
                commitmentChainLength
        );
    }

    @ConditionalOnBean(name = NetworkType.ETHEREUM_ROPSTEN_VALUE)
    @Bean
    public Web3Scanner ethScannerRopsten(
            final @Qualifier(NetworkType.ETHEREUM_ROPSTEN_VALUE) Web3j web3j,
            final @Value("${etherscanner.start-block-dir}") String dir,
            final @Value("${etherscanner.polling-interval-ms:5000}") Long pollingInterval,
            final @Value("${etherscanner.commit-chain-length:5}") Integer commitmentChainLength
    ) {
        return new Web3Scanner(
                NetworkType.ETHEREUM_ROPSTEN,
                web3j,
                new LastBlockPersister(NetworkType.ETHEREUM_ROPSTEN, dir, null),
                pollingInterval,
                commitmentChainLength
        );
    }

    @ConditionalOnBean(name = NetworkType.RSK_MAINNET_VALUE)
    @Bean
    public Web3Scanner rskScanner(
            final @Qualifier(NetworkType.RSK_MAINNET_VALUE) Web3j web3j,
            final @Value("${etherscanner.start-block-dir}") String dir,
            final @Value("${etherscanner.polling-interval-ms:5000}") Long pollingInterval,
            final @Value("${etherscanner.commit-chain-length:5}") Integer commitmentChainLength
    ) {
        return new Web3Scanner(
                NetworkType.RSK_MAINNET,
                web3j,
                new LastBlockPersister(NetworkType.RSK_MAINNET, dir, null),
                pollingInterval,
                commitmentChainLength
        );
    }

    @ConditionalOnBean(name = NetworkType.RSK_TESTNET_VALUE)
    @Bean
    public Web3Scanner rskScannerTestnet(
            final @Qualifier(NetworkType.RSK_TESTNET_VALUE) Web3j web3j,
            final @Value("${etherscanner.start-block-dir}") String dir,
            final @Value("${etherscanner.polling-interval-ms:5000}") Long pollingInterval,
            final @Value("${etherscanner.commit-chain-length:5}") Integer commitmentChainLength
    ) {
        return new Web3Scanner(
                NetworkType.RSK_TESTNET,
                web3j,
                new LastBlockPersister(NetworkType.RSK_TESTNET, dir, null),
                pollingInterval,
                commitmentChainLength
        );
    }
}
