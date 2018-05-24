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
        InvocationTransaction,
        StateTransaction
    }

    private Type type;
    @JsonProperty("txid")
    private String hash;
    @JsonProperty("vout")
    private List<TransactionOutput> outputs;

    @JsonProperty("vin")
    private List<TransactionInput> inputs;
    private List<String> contracts;

    @JsonProperty("script")
    private void extractContracts(final String scriptHex) {
        if (scriptHex == null) return;
        byte[] script = DatatypeConverter.parseHexBinary(scriptHex);
        for (long i = 0; i < script.length; i++) {
            byte opcode = script[(int)i];
            if (opcode >= 0x01 && opcode <= 0x4B) {
                i += opcode;
                continue;
            }
            if (opcode >= 0x4C && opcode <= 0x4E) {
                i++;
                int toRead = (int)Math.pow(2, opcode - 0x4C) & 0xFF;
                i += toRead;
                long count = 0;
                for (int j = 0; j < toRead; j++) {
                    if (i - j - 1 >= script.length) continue;
                    count = (count << 8) + (script[(int)(i - j - 1)] & 0xFF);
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
                byte[] addressBytes = Arrays.copyOfRange(script, (int)i, (int)(i + 20));
                byte[] address = new byte[addressBytes.length];
                for (int j = 0; j < addressBytes.length; j++) {
                    address[j] = addressBytes[addressBytes.length - j - 1];
                }
                contracts.add("0x" + DatatypeConverter.printHexBinary(address).toLowerCase());
                i += addressBytes.length;
            }
        }
    }

    public Transaction() {
        this.contracts = new ArrayList<>();
    }
}
