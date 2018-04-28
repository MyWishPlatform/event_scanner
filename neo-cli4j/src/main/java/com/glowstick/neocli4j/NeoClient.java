package com.glowstick.neocli4j;

public interface NeoClient {
    Integer getBlockCount() throws java.io.IOException;
    String getBlockHash(Integer blockNumber) throws java.io.IOException;
    Block getBlock(String blockHash) throws java.io.IOException;
}
