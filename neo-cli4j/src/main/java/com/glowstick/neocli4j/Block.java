package com.glowstick.neocli4j;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

import java.util.ArrayList;
import java.util.List;

public class Block {
    private final String hash;
    private final Long timestamp;
    private final List<Transaction> transactions;

    public static Block parse(JsonParser parser) {
        String blockHash = null;
        Long blockTimestamp = null;
        List<Transaction> blockTransactions = new ArrayList<>();

        try {
            String name;
            while (parser.nextToken() != JsonToken.END_OBJECT) {
                name = parser.getCurrentName();
                if ("result".equals(name)) {
                    while (parser.nextToken() != JsonToken.END_OBJECT) {
                        name = parser.getCurrentName();
                        if ("script".equals(name)) {
                            while (parser.nextToken() != JsonToken.END_OBJECT);
                        }
                        if ("hash".equals(name)) {
                            parser.nextToken();
                            blockHash = parser.getText();
                        }
                        if ("time".equals(name)) {
                            parser.nextToken();
                            blockTimestamp = Long.valueOf(parser.getText());
                        }
                        if ("tx".equals(name)) {
                            while (parser.nextToken() != JsonToken.END_ARRAY) {
                                String txHash = null;
                                List<TransactionOutput> txOutputs = new ArrayList<>();
                                while (parser.nextToken() != JsonToken.END_OBJECT) {
                                    name = parser.getCurrentName();
                                    if ("attributes".equals(name) || "vin".equals(name) || "scripts".equals(name) || "claims".equals(name)) {
                                        while (parser.nextToken() != JsonToken.END_ARRAY);
                                    }
                                    if ("txid".equals(name)) {
                                        parser.nextToken();
                                        txHash = parser.getText();
                                    }
                                    if ("vout".equals(name)) {
                                        while (parser.nextToken() != JsonToken.END_ARRAY) {
                                            name = parser.getCurrentName();
                                            if ("address".equals(name)) {
                                                parser.nextToken();
                                                txOutputs.add(new TransactionOutput(parser.getText()));
                                            }
                                        }
                                    }
                                }
                                blockTransactions.add(new Transaction(txHash, txOutputs));
                            }
                        }
                    }
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return new Block(blockHash, blockTimestamp, blockTransactions);
    }

    public Block(String hash, Long timestamp, List<Transaction> transactions) {
        this.hash = hash;
        this.timestamp = timestamp;
        this.transactions = transactions;
    }

    public String getHash() {
        return this.hash;
    }

    public Long getTimeSeconds() {
        return this.timestamp;
    }

    public List<Transaction> getTransactions() {
        return this.transactions;
    }
}
