package io.mywish.scanner;

import com.glowstick.neocli4j.NeoClientImpl;
import com.neemre.btcdcli4j.core.client.BtcdClientImpl;
import io.lastwill.eventscan.model.NetworkType;
import io.mywish.scanner.services.scanners.BtcScanner;
import io.mywish.scanner.services.LastBlockPersister;
import io.mywish.scanner.services.scanners.NeoScanner;
import io.mywish.scanner.services.scanners.Web3Scanner;
import io.mywish.wrapper.helpers.BtcBlockParser;
import io.mywish.wrapper.networks.BtcNetwork;
import io.mywish.wrapper.networks.NeoNetwork;
import io.mywish.wrapper.networks.Web3Network;
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
@Import(BtcBlockParser.class)
public class ScannerModule {
    @Bean
    public static PropertySourcesPlaceholderConfigurer propertyConfig() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    @ConditionalOnProperty("etherscanner.bitcoin.rpc-url.mainnet")
    @Bean(name = NetworkType.BTC_MAINNET_VALUE)
    public BtcNetwork btcNetMain(
            final CloseableHttpClient closeableHttpClient,
            final @Value("${etherscanner.bitcoin.rpc-url.mainnet}") URI rpc
    ) throws Exception {
        String user = null, password = null;
        if (rpc.getUserInfo() != null) {
            String[] credentials = rpc.getUserInfo().split(":");
            if (credentials.length > 1) {
                user = credentials[0];
                password = credentials[1];
            }
        }
        return new BtcNetwork(NetworkType.BTC_MAINNET, new BtcdClientImpl(closeableHttpClient, rpc.getScheme(), rpc.getHost(), rpc.getPort(), user, password), new MainNetParams());
    }

    @ConditionalOnProperty("etherscanner.bitcoin.rpc-url.testnet")
    @Bean(name = NetworkType.BTC_TESTNET_3_VALUE)
    public BtcNetwork btcNetTest(
            final CloseableHttpClient closeableHttpClient,
            final @Value("${etherscanner.bitcoin.rpc-url.testnet}") URI rpc
    ) throws Exception {
        String user = null, password = null;
        if (rpc.getUserInfo() != null) {
            String[] credentials = rpc.getUserInfo().split(":");
            if (credentials.length > 1) {
                user = credentials[0];
                password = credentials[1];
            }
        }
        return new BtcNetwork(NetworkType.BTC_TESTNET_3, new BtcdClientImpl(closeableHttpClient, rpc.getScheme(), rpc.getHost(), rpc.getPort(), user, password), new TestNet3Params());
    }


    @ConditionalOnProperty(name = "io.lastwill.eventscan.web3-url.ethereum")
    @Bean(name = NetworkType.ETHEREUM_MAINNET_VALUE)
    public Web3Network ethNetMain(@Value("${io.lastwill.eventscan.web3-url.ethereum}") String web3Url) {
        return new Web3Network(NetworkType.ETHEREUM_MAINNET, Web3j.build(new HttpService(web3Url)));
    }

    @ConditionalOnProperty(name = "io.lastwill.eventscan.web3-url.ropsten")
    @Bean(name = NetworkType.ETHEREUM_ROPSTEN_VALUE)
    public Web3Network ethNetRopsten(@Value("${io.lastwill.eventscan.web3-url.ropsten}") String web3Url) {
        return new Web3Network(NetworkType.ETHEREUM_ROPSTEN, Web3j.build(new HttpService(web3Url)));
    }

    @ConditionalOnProperty(name = "io.lastwill.eventscan.web3-url.rsk-mainnet")
    @Bean(name = NetworkType.RSK_MAINNET_VALUE)
    public Web3Network rskNetMain(@Value("${io.lastwill.eventscan.web3-url.rsk-mainnet}") String web3Url) {
        return new Web3Network(NetworkType.RSK_MAINNET, Web3j.build(new HttpService(web3Url)));
    }

    @ConditionalOnProperty(name = "io.lastwill.eventscan.web3-url.rsk-testnet")
    @Bean(name = NetworkType.RSK_TESTNET_VALUE)
    public Web3Network rskNetTest(@Value("${io.lastwill.eventscan.web3-url.rsk-testnet}") String web3Url) {
        return new Web3Network(NetworkType.RSK_TESTNET, Web3j.build(new HttpService(web3Url)));
    }

    @ConditionalOnProperty(name = "etherscanner.neo.rpc-url.mainnet")
    @Bean(name = NetworkType.NEO_MAINNET_VALUE)
    public NeoNetwork neoNetMain(
            final CloseableHttpClient closeableHttpClient,
            final @Value("${etherscanner.neo.rpc-url.mainnet}") URI rpc
    ) {
        return new NeoNetwork(NetworkType.NEO_MAINNET, new NeoClientImpl(closeableHttpClient, rpc));
    }

    @ConditionalOnProperty(name = "etherscanner.neo.rpc-url.testnet")
    @Bean(name = NetworkType.NEO_TESTNET_VALUE)
    public NeoNetwork neoNetTest(
            final CloseableHttpClient closeableHttpClient,
            final @Value("${etherscanner.neo.rpc-url.testnet}") URI rpc
    ) {
        return new NeoNetwork(NetworkType.NEO_TESTNET, new NeoClientImpl(closeableHttpClient, rpc));
    }

    @ConditionalOnBean(name = NetworkType.BTC_MAINNET_VALUE)
    @Bean
    public BtcScanner btcScannerMain(
            final @Qualifier(NetworkType.BTC_MAINNET_VALUE) BtcNetwork network,
            final @Value("${etherscanner.start-block-dir}") String dir,
            final @Value("${etherscanner.bitcoin.last-block.mainnet:#{null}}") Long lastBlock,
            final @Value("${etherscanner.bitcoin.polling-interval-ms}") Long pollingInterval,
            final @Value("${etherscanner.bitcoin.commit-chain-length}") Integer commitmentChainLength
    ) throws Exception {
        return new BtcScanner(
                network,
                new LastBlockPersister(network.getType(), dir, lastBlock),
                pollingInterval,
                commitmentChainLength
        );
    }

    @ConditionalOnBean(name = NetworkType.BTC_TESTNET_3_VALUE)
    @Bean
    public BtcScanner btcScannerTest(
            final @Qualifier(NetworkType.BTC_TESTNET_3_VALUE) BtcNetwork network,
            final @Value("${etherscanner.start-block-dir}") String dir,
            final @Value("${etherscanner.bitcoin.last-block.testnet:#{null}}") Long lastBlock,
            final @Value("${etherscanner.bitcoin.polling-interval-ms}") Long pollingInterval,
            final @Value("${etherscanner.bitcoin.commit-chain-length}") Integer commitmentChainLength
    ) throws Exception {
        return new BtcScanner(
                network,
                new LastBlockPersister(network.getType(), dir, lastBlock),
                pollingInterval,
                commitmentChainLength
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

    @ConditionalOnBean(name = NetworkType.ETHEREUM_MAINNET_VALUE)
    @Bean
    public Web3Scanner ethScannerMain(
            final @Qualifier(NetworkType.ETHEREUM_MAINNET_VALUE) Web3Network network,
            final @Value("${etherscanner.start-block-dir}") String dir,
            final @Value("${etherscanner.polling-interval-ms:5000}") Long pollingInterval,
            final @Value("${etherscanner.commit-chain-length:5}") Integer commitmentChainLength
    ) {
        return new Web3Scanner(
                network,
                new LastBlockPersister(network.getType(), dir, null),
                pollingInterval,
                commitmentChainLength
        );
    }

    @ConditionalOnBean(name = NetworkType.ETHEREUM_ROPSTEN_VALUE)
    @Bean
    public Web3Scanner ethScannerRopsten(
            final @Qualifier(NetworkType.ETHEREUM_ROPSTEN_VALUE) Web3Network network,
            final @Value("${etherscanner.start-block-dir}") String dir,
            final @Value("${etherscanner.polling-interval-ms:5000}") Long pollingInterval,
            final @Value("${etherscanner.commit-chain-length:5}") Integer commitmentChainLength
    ) {
        return new Web3Scanner(
                network,
                new LastBlockPersister(network.getType(), dir, null),
                pollingInterval,
                commitmentChainLength
        );
    }

    @ConditionalOnBean(name = NetworkType.RSK_MAINNET_VALUE)
    @Bean
    public Web3Scanner rskScannerMain(
            final @Qualifier(NetworkType.RSK_MAINNET_VALUE) Web3Network network,
            final @Value("${etherscanner.start-block-dir}") String dir,
            final @Value("${etherscanner.polling-interval-ms:5000}") Long pollingInterval,
            final @Value("${etherscanner.commit-chain-length:5}") Integer commitmentChainLength
    ) {
        return new Web3Scanner(
                network,
                new LastBlockPersister(network.getType(), dir, null),
                pollingInterval,
                commitmentChainLength
        );
    }

    @ConditionalOnBean(name = NetworkType.RSK_TESTNET_VALUE)
    @Bean
    public Web3Scanner rskScannerTest(
            final @Qualifier(NetworkType.RSK_TESTNET_VALUE) Web3Network network,
            final @Value("${etherscanner.start-block-dir}") String dir,
            final @Value("${etherscanner.polling-interval-ms:5000}") Long pollingInterval,
            final @Value("${etherscanner.commit-chain-length:5}") Integer commitmentChainLength
    ) {
        return new Web3Scanner(
                network,
                new LastBlockPersister(network.getType(), dir, null),
                pollingInterval,
                commitmentChainLength
        );
    }
}
