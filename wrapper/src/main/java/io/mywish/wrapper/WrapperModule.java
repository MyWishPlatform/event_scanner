package io.mywish.wrapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.neemre.btcdcli4j.core.client.BtcdClientImpl;
import io.lastwill.eventscan.model.NetworkType;
import io.mywish.eoscli4j.service.EosClientImpl;
import io.mywish.neocli4j.NeoClientImpl;
import io.mywish.wrapper.networks.BtcNetwork;
import io.mywish.wrapper.networks.EosNetwork;
import io.mywish.wrapper.networks.NeoNetwork;
import io.mywish.wrapper.networks.Web3Network;
import io.mywish.wrapper.parity.Web3jEx;
import okhttp3.OkHttpClient;
import org.apache.http.impl.client.CloseableHttpClient;
import org.bitcoinj.params.MainNetParams;
import org.bitcoinj.params.TestNet3Params;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;

import java.net.URI;

@ComponentScan
@Configuration
@PropertySource("classpath:scanner.properties")
public class WrapperModule {
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
        return new BtcNetwork(
                NetworkType.BTC_MAINNET,
                new BtcdClientImpl(
                        closeableHttpClient,
                        rpc.getScheme(),
                        rpc.getHost(),
                        rpc.getPort(),
                        user,
                        password
                ),
                new MainNetParams()
        );
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
        return new BtcNetwork(
                NetworkType.BTC_TESTNET_3,
                new BtcdClientImpl(
                        closeableHttpClient,
                        rpc.getScheme(),
                        rpc.getHost(),
                        rpc.getPort(),
                        user,
                        password
                ),
                new TestNet3Params());
    }


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

    @ConditionalOnProperty(name = "etherscanner.neo.rpc-url.mainnet")
    @Bean(name = NetworkType.NEO_MAINNET_VALUE)
    public NeoNetwork neoNetMain(
            final CloseableHttpClient closeableHttpClient,
            final ObjectMapper objectMapper,
            final @Value("${etherscanner.neo.rpc-url.mainnet}") URI rpc
    ) {
        return new NeoNetwork(
                NetworkType.NEO_MAINNET,
                new NeoClientImpl(
                        closeableHttpClient,
                        rpc,
                        objectMapper
                )
        );
    }

    @ConditionalOnProperty(name = "etherscanner.neo.rpc-url.testnet")
    @Bean(name = NetworkType.NEO_TESTNET_VALUE)
    public NeoNetwork neoNetTest(
            final CloseableHttpClient closeableHttpClient,
            final ObjectMapper objectMapper,
            final @Value("${etherscanner.neo.rpc-url.testnet}") URI rpc
    ) {
        return new NeoNetwork(
                NetworkType.NEO_TESTNET,
                new NeoClientImpl(
                        closeableHttpClient,
                        rpc,
                        objectMapper
                )
        );
    }

    @ConditionalOnProperty(name = "etherscanner.eos.rpc-url.testnet")
    @Bean(name = NetworkType.EOS_TESTNET_VALUE)
    public EosNetwork eosNetTest(
            final CloseableHttpClient closeableHttpClient,
            final ObjectMapper objectMapper,
            final @Value("${etherscanner.eos.tcp-host.testnet}") String tcpHost,
            final @Value("${etherscanner.eos.tcp-port.testnet}") int tcpPort,
            final @Value("${etherscanner.eos.rpc-url.testnet}") URI rpc
    ) throws Exception {
        return new EosNetwork(
                NetworkType.EOS_TESTNET,
                new EosClientImpl(
                        tcpHost,
                        tcpPort,
                        closeableHttpClient,
                        rpc,
                        objectMapper
                )
        );
    }

    @ConditionalOnProperty(name = "etherscanner.eos.rpc-url.mainnet")
    @Bean(name = NetworkType.EOS_MAINNET_VALUE)
    public EosNetwork eosNetMain(
            final CloseableHttpClient closeableHttpClient,
            final ObjectMapper objectMapper,
            final @Value("${etherscanner.eos.tcp-host.mainnet}") String tcpHost,
            final @Value("${etherscanner.eos.tcp-port.mainnet}") int tcpPort,
            final @Value("${etherscanner.eos.rpc-url.mainnet}") URI rpc
    ) throws Exception {
        return new EosNetwork(
                NetworkType.EOS_MAINNET,
                new EosClientImpl(
                        tcpHost,
                        tcpPort,
                        closeableHttpClient,
                        rpc,
                        objectMapper
                )
        );
    }
}
