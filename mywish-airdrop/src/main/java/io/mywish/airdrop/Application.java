package io.mywish.airdrop;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.mywish.airdrop.model.AirdropEntry;
import io.mywish.airdrop.services.EosAdapter;
import io.mywish.airdrop.services.EosishAirdropService;
import io.mywish.airdrop.services.WishTransferFetcher;
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
import java.util.EnumSet;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@EntityScan
@SpringBootApplication
@Import({ScannerModule.class, })
public class Application implements CommandLineRunner {
    public enum AirdropType {
        bounty,
        wish,
        eos,
        fetch
    }
    @Autowired
    private EosishAirdropService eosishAirdropService;
    @Autowired
    private WishTransferFetcher wishTransferFetcher;

    @Value("${limit:#{null}}")
    private Integer limit;

    @Value("${type:#{null}}")
    private AirdropType type;

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
        if (type == null) {
            log.error("Use --type with one of {} values.", EnumSet.allOf(AirdropType.class).stream().map(Enum::toString).collect(Collectors.joining(", ")));
            System.exit(-1);
            return;
        }
        try {
            List<AirdropEntry> entries;
            switch (type) {
                case wish:
                    entries = eosishAirdropService.findFistWish(limit);
                    break;
                case bounty:
                    entries = eosishAirdropService.findFistBounty(limit);
                    break;
                case eos:
                    entries = eosishAirdropService.findFistEos(limit);
                    break;
                case fetch:
                    wishTransferFetcher.fetch(4397737, 6526408);
                default:
                    throw new UnsupportedOperationException(type + " not supported now");
            }
            log.info("Found {} entries to update.", entries.size());
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
                                .setConnectTimeout(1000)
                                .setSocketTimeout(500)
                                .setConnectionRequestTimeout(1000)
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
