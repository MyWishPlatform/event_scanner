package com.glowstick.neocli4j;

import java.util.List;

public interface NeoClient {
    Integer getBlockCount() throws java.io.IOException;
    String getBlockHash(Integer blockNumber) throws java.io.IOException;
    Block getBlock(String blockHash) throws java.io.IOException;
    Transaction getTransaction(String txHash) throws java.io.IOException;
    List<Event> getEvents(String txHash) throws java.io.IOException;
}
