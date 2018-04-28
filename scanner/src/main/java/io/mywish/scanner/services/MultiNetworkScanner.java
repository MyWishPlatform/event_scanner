package io.mywish.scanner.services;

import io.mywish.scanner.model.NetworkType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.web3j.protocol.Web3j;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@Slf4j
public class MultiNetworkScanner {
    @Value("${etherscanner.polling-interval-ms:5000}")
    private long pollingInterval;

    @Value("${etherscanner.commit-chain-length:5}")
    private int commitmentChainLength;

    @Value("${etherscanner.start-block-dir:last-blocks}")
    private String startBlockFileDir;

    @Autowired
    private Map<String, Web3j> web3jByName = new HashMap<>();

    private List<Web3Scanner> scanners;

    @PostConstruct
    protected void init() {
        scanners = web3jByName.entrySet()
                .stream()
                .map(entry -> new Web3Scanner(
                        NetworkType.valueOf(entry.getKey()),
                        entry.getValue(),
                        new LastBlockPersister(NetworkType.valueOf(entry.getKey()), startBlockFileDir, null),
                        pollingInterval,
                        commitmentChainLength)
                )
                .filter(scanner -> {
                    try {
                        scanner.open();
                    }
                    catch (Exception e) {
                        log.warn("Opening scanner {} for network {} was failed. Skip it.", scanner, scanner.getNetworkType(), e);
                        return false;
                    }
                    return true;
                })
                .collect(Collectors.toList());

        if (scanners.isEmpty()) {
            throw new IllegalStateException("There is no scanners at all.");
        }
    }

    @PreDestroy
    public void close() {
        log.info("Close web3 scanners.");
        scanners.parallelStream().forEach(scanner -> {
            try {
                scanner.close();
            }
            catch (Exception e) {
                log.error("Closing scanner {} for network {} was failed.", scanner, scanner.getNetworkType(), e);
            }
        });
    }
}
