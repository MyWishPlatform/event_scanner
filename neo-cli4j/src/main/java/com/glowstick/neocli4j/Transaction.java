package com.glowstick.neocli4j;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import javax.xml.bind.DatatypeConverter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
public class Transaction {
    public enum Type {
        MinerTransaction,
        IssueTransaction,
        ClaimTransaction,
        EnrollmentTransaction,
        RegisterTransaction,
        ContractTransaction,
        AgencyTransaction,
        PublishTransaction,
        InvocationTransaction
    }

    private Type type;
    @JsonProperty("txid")
    private String hash;
    @JsonProperty("vout")
    private List<TransactionOutput> outputs;
    private List<String> contracts;

    @JsonProperty("script")
    public void extractContracts(final String scriptHex) {
        if (scriptHex == null) return;
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
                    count = (count << 8) + (script[i + j] & 0xFF);
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

    public Transaction() {
        this.contracts = new ArrayList<>();
    }
}
