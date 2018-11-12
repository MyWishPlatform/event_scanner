package io.mywish.airdrop.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.mywish.airdrop.model.WishTransfer;
import io.mywish.airdrop.model.fetcher.LogEntry;
import io.mywish.airdrop.model.fetcher.LogsResult;
import io.mywish.airdrop.repositories.WishTransferRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.xml.bind.DatatypeConverter;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class WishTransferFetcher {
    private final long PORTION = 10000;

    @Autowired
    private HttpClient httpClient;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private WishTransferRepository wishTransferRepository;

    @Value("${io.mywish.airdrop.api-key}")
    private String apiKey;

    public void fetch(long fromBlock, long toBlock) throws IOException {
        if (fromBlock > toBlock) {
            throw new IllegalArgumentException("fromBlock must be less then toBlock");
        }
        for (long i = fromBlock; i < toBlock + PORTION; i += PORTION) {
            List<WishTransfer> transfers = fetchPortion(i, Math.min(i + PORTION, toBlock))
                    .stream()
                    .map(this::convert)
                    .collect(Collectors.toList());
            wishTransferRepository.save(transfers);
        }
    }

    private List<LogEntry> fetchPortion(long fromBlock, long toBlock) throws IOException {

        HttpResponse response = httpClient.execute(new HttpGet(
                "https://api.etherscan.io/api?module=logs&action=getLogs&fromBlock="
                        + fromBlock
                        + "&toBlock="
                        + toBlock
                        + "&address=0x1b22c32cd936cb97c28c5690a0695a82abf688e6&topic0=0xddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3ef"
                        + "&apikey=" + apiKey));
        LogsResult result = objectMapper.readValue(response.getEntity().getContent(), LogsResult.class);
        if (result.getResult().size() == 1000) {
            List<LogEntry> more = new ArrayList<>(2000);
            long diff = toBlock - fromBlock;
            more.addAll(fetchPortion(fromBlock, fromBlock + diff / 2));
            more.addAll(fetchPortion(fromBlock + diff / 2 + 1, toBlock));
            return more;
        }
        return result.getResult();
    }

    private WishTransfer convert(LogEntry logEntry) {
        String from = toAddress(logEntry.getTopics().get(1));
        String to = toAddress(logEntry.getTopics().get(2));
        BigInteger amount = new BigInteger(DatatypeConverter.parseHexBinary(logEntry.getData()));
        long block = new BigInteger(DatatypeConverter.parseHexBinary(logEntry.getBlockNumber())).longValue();
        String txHash = logEntry.getTransactionHash();
        return new WishTransfer(null, from, to, amount, txHash, block);
    }

    private String toAddress(String address32bytes) {
        return "0x" + address32bytes.substring(address32bytes.length() - 40);
    }
}
