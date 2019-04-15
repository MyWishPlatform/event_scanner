package io.lastwill.eventscan.model;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
public class ProductStatistics {
    public final static Map<Integer, String> PRODUCT_TYPES = new HashMap<Integer, String>() {{
        put(0, "LastWill");
        put(1, "Wallet");
        put(2, "Delayed Payment");
        put(3, "Pizza");
        put(4, "ICO");
        put(5, "Token");
        put(6, "NEO Token");
        put(7, "NEO ICO");
        put(8, "Airdrop");
        put(9, "Investment Pool");
        put(10, "EOS Token");
        put(11, "EOS Account");
        put(12, "EOS ICO");
        put(13, "EOS Airdrop");
        put(14, "EOS Ext Token");
        put(15, "TRON Token");
        put(16, "TRON Game Asset");
        put(17, "TRON Airdrop");
        put(18, "TRON LostKey Tokens");
        put(19, "LostKey Tokens");
        put(20, "SWAPS");
        put(21, "STO");
    }};

    private final Integer contractType;
    private final String contractState;
    private final Integer contractCount;

    public ProductStatistics(Integer contractType, String contractState, Integer contractCount) {
        this.contractType = contractType;
        this.contractState = contractState;
        this.contractCount = contractCount;
    }

    public String getContractType() {
        return PRODUCT_TYPES.getOrDefault(contractType, "Undefined " + contractType);
    }
}
