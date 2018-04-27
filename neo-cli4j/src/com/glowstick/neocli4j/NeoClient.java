package com.glowstick.neocli4j;

public interface NeoClient {
    Integer getBlockCount();
    String getBlockHash(Integer blockNumber);
    Block getBlock(String blockHash);
}
