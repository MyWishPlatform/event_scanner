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
