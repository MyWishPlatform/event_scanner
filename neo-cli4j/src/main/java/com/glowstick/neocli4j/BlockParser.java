package com.glowstick.neocli4j;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;

public class BlockParser {
    public Block parse(String json) throws java.io.IOException {
        JsonNode node = new ObjectMapper().readTree(json).get("result");
        String hash = node.get("hash").textValue();
        Long time = node.get("time").longValue();
        List<Transaction> tx = new ArrayList<>();
        if (node.get("tx").isArray()) node.get("tx").forEach(txNode -> {
            Transaction.Type txType = null;
            String txHash = txNode.get("txid").textValue();
            List<TransactionOutput> txOutputs = new ArrayList<>();
            String txScript = null;

            String type = txNode.get("type").textValue();
            if ("MinerTransaction".equals(type)) txType = Transaction.Type.Miner;
            else if ("ClaimTransaction".equals(type)) txType = Transaction.Type.Claim;
            else if ("ContractTransaction".equals(type)) txType = Transaction.Type.Contract;
            else if ("InvocationTransaction".equals(type)) txType = Transaction.Type.Invocation;

            txNode.get("vout").forEach(txOut -> txOutputs.add(new TransactionOutput(
                    txOut.get("address").textValue(),
                    txOut.get("asset").textValue(),
                    txOut.get("value").asDouble()
            )));

            if (txNode.get("script") != null) txScript = txNode.get("script").textValue();

            tx.add(new Transaction(txType, txHash, txOutputs, txScript));
        });
        return new Block(hash, time, tx);
    }
}
