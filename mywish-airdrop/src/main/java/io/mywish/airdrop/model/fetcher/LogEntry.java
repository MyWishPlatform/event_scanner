package io.mywish.airdrop.model.fetcher;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class LogEntry {
    private String address;
    private List<String> topics;
    private String data;
    private String blockNumber;
    private String transactionHash;
}
