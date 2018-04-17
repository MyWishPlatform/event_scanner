package io.mywish.scanner;

import com.neemre.btcdcli4j.core.BitcoindException;
import com.neemre.btcdcli4j.core.CommunicationException;
import com.neemre.btcdcli4j.core.client.BtcdClient;
import com.neemre.btcdcli4j.core.client.BtcdClientImpl;
import io.mywish.scanner.model.NetworkType;
import io.mywish.scanner.services.BtcScanner;
import io.mywish.scanner.services.LastBlockPersister;
import org.apache.http.impl.client.CloseableHttpClient;
import org.bitcoinj.core.Context;
import org.bitcoinj.params.TestNet3Params;
import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

import java.net.URI;
import java.net.URISyntaxException;

@Configuration
@ComponentScan
@PropertySource("classpath:scanner.properties")
public class ScannerModule {
    @Bean
    public static PropertySourcesPlaceholderConfigurer propertyConfig() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    @Bean
    public BtcdClient btcdClient(
            final CloseableHttpClient closeableHttpClient,
            final @Value("${etherscanner.bitcoin.rpc-url}") URI rpc
    ) throws BitcoindException, CommunicationException, URISyntaxException {
//        URI rpc = new URI("http://bitcoin:btcpwd@127.0.0.1:18332");
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
    public TestNet3Params networkParameters() {
        return new TestNet3Params();
    }

    @Bean
    public LastBlockPersister btcLastBlockPersisterTestnet(@Value("${etherscanner.start-block-dir}") String dir, @Value("${etherscanner.bitcoin.last-block:#{null}}") Long lastBlock) {
        return new LastBlockPersister(NetworkType.BTC_TESTNET_3, dir, lastBlock);
    }

    @Bean
    public BtcScanner btcScanner(
            @Qualifier("btcLastBlockPersisterTestnet") LastBlockPersister lastBlockPersister,
            TestNet3Params testNet3Params
    ) {
        // not explicit dependant
        Context.getOrCreate(testNet3Params);
        return new BtcScanner(NetworkType.BTC_TESTNET_3, lastBlockPersister, testNet3Params);
    }

//    @Bean
//    public BlockChain blockChain(NetworkParameters networkParameters) throws BlockStoreException {
//        return new BlockChain(networkParameters, new MemoryFullPrunedBlockStore(networkParameters, 100));
//    }
//
//    @Bean
//    public PeerGroup peerGroup(NetworkParameters networkParameters, BlockChain blockChain) {
//        return new PeerGroup(networkParameters, blockChain);
//    }
}
