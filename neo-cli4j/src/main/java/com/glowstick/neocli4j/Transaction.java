package com.glowstick.neocli4j;

import javax.xml.bind.DatatypeConverter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Transaction {
    public enum Type {
        Miner,
        Issue,
        Claim,
        Enrollment,
        Register,
        Contract,
        Agency,
        Publish,
        Invocation
    }

    private final Type type;
    private final String hash;
    private final List<TransactionOutput> outputs;
    private final List<String> contracts;
    private final String script;

    private static List<String> extractContracts(String scriptHex) {
        List<String> contracts = new ArrayList<>();
        if (scriptHex != null) {
            byte[] script = DatatypeConverter.parseHexBinary(scriptHex);
            for (int i = 0; i < script.length; i++) {
                byte opcode = script[i];
                if (opcode >= 0x01 && opcode <= 0x4B) {
                    i += opcode;
                    continue;
                }
                if (opcode >= 0x4C && opcode <= 0x4E) {
                    i++;
                    long count = 0;
                    for (int j = 0; j < Math.pow(2, opcode - 0x4C); j++) {
                        count = (count << 8) + script[i + j];
                    }
                    i += count;
                    continue;
                }
                if (opcode >= 0x62 && opcode <= 0x65) {
                    i += 3;
                    continue;
                }
                if (opcode >= 0x67 && opcode <= 0x69) {
                    i++;
                    byte[] addressBytes = Arrays.copyOfRange(script, i, i + 20);
                    byte[] address = new byte[addressBytes.length];
                    for (int j = 0; j < addressBytes.length; j++) {
                        address[j] = addressBytes[addressBytes.length - j - 1];
                    }
                    contracts.add(DatatypeConverter.printHexBinary(address).toLowerCase());
                    i += addressBytes.length;
                }
            }
        }
        return contracts;
    }

    public Transaction(Type type, String hash, List<TransactionOutput> outputs, String script) {
        this.type = type;
        this.hash = hash;
        this.outputs = outputs;
        this.script = script;
        this.contracts = extractContracts(script);
    }

    public Type getType() {
        return this.type;
    }

    public String getHash() {
        return this.hash;
    }

    public List<TransactionOutput> getOutputs() {
        return this.outputs;
    }

    public String getScript() {
        return this.script;
    }

    public List<String> getContracts() {
        return this.contracts;
    }
}
