package io.mywish.neocli4j;

import java.math.BigInteger;
import java.util.List;

public interface NeoClient {
    Integer getBlockCount() throws java.io.IOException;
    Block getBlock(String blockHash) throws java.io.IOException;
    Block getBlock(Long blockNumber) throws java.io.IOException;
    Transaction getTransaction(String txHash, boolean getInputs) throws java.io.IOException;
    List<Event> getEvents(String txHash) throws java.io.IOException;
    BigInteger getBalance(String address) throws java.io.IOException;
}
