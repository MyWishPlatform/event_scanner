package io.mywish.airdrop.model.fetcher;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class LogsResult {
    private int status;
    private String message;
    private List<LogEntry> result;
}
