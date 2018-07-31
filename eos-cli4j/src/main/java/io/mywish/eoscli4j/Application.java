package io.mywish.eoscli4j;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.mywish.eoscli4j.model.response.BlockResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClients;

import java.net.URI;

public class Application {
    public static void main(String[] args) {
        try {
            HttpClient httpClient = HttpClients.createDefault();
            ObjectMapper mapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            EosClient client = new EosClientImpl("127.0.0.1", 56732, httpClient, new URI("http://127.0.0.1:8887"), mapper);
            client.subscribe(7326769, block -> {
                System.out.println(block.getBlockNum());
            });
            /*
            ChainInfoResponse chainInfo = client.getChainInfo();
            BlockResponse block = client.getBlock(2036856L);
            BalanceResponse balance = client.getBalance("eosio.token", "bitfinexeos1");
            System.out.println(chainInfo.getHeadBlockNum());
            System.out.println(balance.getValue() + " (" + balance.getDecimals() + ")");
            System.out.println(block.getTransactions().size());
            for (Transaction transaction : block.getTransactions()) {
                System.out.println(transaction.getTrx().getId());
            }*/
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
