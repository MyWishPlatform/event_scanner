package io.mywish.neocli4j;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClients;

import java.net.URI;

public class Application {
    public static void main(String[] args) {
        try {
            HttpClient httpClient = HttpClients.createDefault();
//            NeoClient neoCli = new NeoClientImpl(httpClient, new URI("http://localhost:20332"), new ObjectMapper());
            NeoClient neoCli = new NeoClientImpl(httpClient, new URI("http://seed2.neo.org:20332"), new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false));
//            Block block = neoCli.getBlock("0xcf94e09f53eb011ef77c017e9542667bb87b5bbc7399fc303740a8a7d237c7d4");
//            Transaction tx = neoCli.getTransaction("0x60c2addec8a05ffab03d925bd62aa9d637bbeee273cc9820965b4dfca929aa8a");
//            neoCli.getEvents("5bec877d8fa6ef592e299605ca5e1bd182b0de6bdad21ef56ecc51b05e63f07e").forEach(x -> {
            Block block = neoCli.getBlock(1492757L);
            System.out.println("Block: " + block.getHash());
            System.out.println("Timestamp: " + block.getTimestamp());
            for (Transaction tx : block.getTransactions()) {
                tx.getContracts().forEach(System.out::println);
                System.out.println("  Tx: " + tx.getHash());
                for (TransactionOutput txOut : tx.getOutputs()) {
                    System.out.println("    Out: " + txOut.getAddress() + ", " + txOut.getValue());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
