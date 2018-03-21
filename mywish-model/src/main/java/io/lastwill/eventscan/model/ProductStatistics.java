package io.lastwill.eventscan.model;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
public class ProductStatistics {
    public final static Map<Integer, String> PRODUCT_TYPES = new HashMap<Integer, String>() {{
        put(0, "MyWish Original");
        put(1, "MyWish Wallet");
        put(2, "MyWish Delayed Payment");
        put(3, "Pizza");
        put(4, "MyWish ICO");
        put(5, "MyWish Token");
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
