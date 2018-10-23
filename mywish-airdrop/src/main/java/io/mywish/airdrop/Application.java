package io.mywish.airdrop;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.mywish.airdrop.services.EosAdapter;
import io.mywish.airdrop.services.EosishAirdropService;
import io.mywish.scanner.ScannerModule;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

import java.net.URL;
import java.security.Security;

@Slf4j
@EntityScan
@SpringBootApplication
@Import({ScannerModule.class, })
public class Application implements CommandLineRunner {
    @Autowired
    private EosishAirdropService eosishAirdropService;

    @Value("${limit:#{null}}")
    private Integer limit;

    public static void main(String[] args) {
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
        new SpringApplicationBuilder()
                .main(Application.class)
                .sources(Application.class)
                .web(false)
                .build(args)
                .run(args);
    }

    @ConditionalOnProperty(name = {"etherscanner.eos.rpc-url.mainnet", })
    @Bean
    public EosAdapter eosAdapterMainnet(@Value("${etherscanner.eos.rpc-url.mainnet}") URL nodeURL) {
        return new EosAdapter(nodeURL);
    }

    @ConditionalOnProperty(name = {"etherscanner.eos.rpc-url.testnet", })
    @Bean
    public EosAdapter eosAdapterTestnet(@Value("${etherscanner.eos.rpc-url.testnet}") URL nodeURL) {
        return new EosAdapter(nodeURL);
    }

    @Override
    public void run(String... args) throws Exception {
        if (limit == null) {
            log.error("Use --limit argument to specify how many rows will be process.");
            System.exit(-1);
            return;
        }
        try {

            val entries = eosishAirdropService.findFist(limit);
            eosishAirdropService.update(entries);
        }
        catch (Exception e) {
            log.error("Error during airdrop.", e);
            System.exit(0);
        }
        log.info("Sending done. Waiting until all transaction will be in irreversible blocks.");
    }

    @Bean(destroyMethod = "close")
    public CloseableHttpClient closeableHttpClient() {
        return HttpClientBuilder
                .create()
                .setMaxConnPerRoute(50)
                .setMaxConnTotal(200)
                .setDefaultRequestConfig(
                        RequestConfig.custom()
                                .setConnectTimeout(10000)
                                .setSocketTimeout(5000)
                                .setConnectionRequestTimeout(10000)
                                .build()
                )
                .setConnectionManagerShared(true)
                .build();
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper()
                // TODO: remove it!
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .registerModule(new JavaTimeModule());
    }
}
