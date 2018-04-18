package io.mywish.scanner;

import com.neemre.btcdcli4j.core.BitcoindException;
import com.neemre.btcdcli4j.core.CommunicationException;
import com.neemre.btcdcli4j.core.client.BtcdClient;
import com.neemre.btcdcli4j.core.client.BtcdClientImpl;
import io.mywish.scanner.model.NetworkType;
import io.mywish.scanner.services.BtcScanner;
import io.mywish.scanner.services.LastBlockPersister;
import org.apache.http.impl.client.CloseableHttpClient;
import org.bitcoinj.params.MainNetParams;
import org.bitcoinj.params.TestNet3Params;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

import java.net.URI;

@Configuration
@ComponentScan
@PropertySource("classpath:scanner.properties")
public class ScannerModule {
    @Bean
    public static PropertySourcesPlaceholderConfigurer propertyConfig() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    @ConditionalOnProperty("etherscanner.bitcoin.rpc-url.mainnet")
    @Bean
    public BtcdClient btcdClient(
            final CloseableHttpClient closeableHttpClient,
            final @Value("${etherscanner.bitcoin.rpc-url.mainnet}") URI rpc
    ) throws BitcoindException, CommunicationException {
        String user = null, password = null;
        if (rpc.getUserInfo() != null) {
            String[] credentials = rpc.getUserInfo().split(":");
            if (credentials.length > 1) {
                user = credentials[0];
                password = credentials[1];
            }
        }
        return new BtcdClientImpl(
                closeableHttpClient,
                rpc.getScheme(),
                rpc.getHost(),
                rpc.getPort(),
                user,
                password
        );
    }

    @ConditionalOnProperty("etherscanner.bitcoin.rpc-url.testnet")
    @Bean
    public BtcdClient btcdClientTestnet(
            final CloseableHttpClient closeableHttpClient,
            final @Value("${etherscanner.bitcoin.rpc-url.testnet}") URI rpc
    ) throws BitcoindException, CommunicationException {
        String user = null, password = null;
        if (rpc.getUserInfo() != null) {
            String[] credentials = rpc.getUserInfo().split(":");
            if (credentials.length > 1) {
                user = credentials[0];
                password = credentials[1];
            }
        }
        return new BtcdClientImpl(
                closeableHttpClient,
                rpc.getScheme(),
                rpc.getHost(),
                rpc.getPort(),
                user,
                password
        );
    }

    @Bean
    public TestNet3Params testNet3Params() {
        return new TestNet3Params();
    }

    @Bean
    public MainNetParams mainNetParams() {
        return new MainNetParams();
    }

    @Bean
    public LastBlockPersister btcLastBlockPersisterTestnet(
            @Value("${etherscanner.start-block-dir}") String dir,
            @Value("${etherscanner.bitcoin.last-block.testnet:#{null}}") Long lastBlock) {
        return new LastBlockPersister(NetworkType.BTC_TESTNET_3, dir, lastBlock);
    }

    @Bean
    public LastBlockPersister btcLastBlockPersister(
            @Value("${etherscanner.start-block-dir}") String dir,
            @Value("${etherscanner.bitcoin.last-block.mainnet:#{null}}") Long lastBlock) {
        return new LastBlockPersister(NetworkType.BTC_MAINNET, dir, lastBlock);
    }

    @ConditionalOnBean(name = "btcdClientTestnet")
    @Bean
    public BtcScanner btcScannerTestnet(
            @Qualifier("btcdClientTestnet") BtcdClient btcdClient,
            @Qualifier("btcLastBlockPersisterTestnet") LastBlockPersister lastBlockPersister,
            TestNet3Params params
    ) {
        return new BtcScanner(btcdClient, NetworkType.BTC_TESTNET_3, lastBlockPersister, params);
    }

    @ConditionalOnBean(name = "btcdClient")
    @Bean
    public BtcScanner btcScanner(
            @Qualifier("btcdClient") BtcdClient btcdClient,
            @Qualifier("btcLastBlockPersister") LastBlockPersister lastBlockPersister,
            MainNetParams params
    ) {
        return new BtcScanner(btcdClient, NetworkType.BTC_MAINNET, lastBlockPersister, params);
    }
}
