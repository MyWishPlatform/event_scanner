package io.mywish.eoscli4j;

import io.mywish.eoscli4j.model.BlockReliability;
import io.mywish.eoscli4j.model.response.ChainInfoResponse;
import io.mywish.eoscli4j.model.response.BalanceResponse;
import io.mywish.eoscli4j.model.response.BlockResponse;

public interface EosClient {
    ChainInfoResponse getChainInfo() throws Exception;
    BlockResponse getBlock(String hash) throws Exception;
    BlockResponse getBlock(Long number) throws Exception;
    BalanceResponse getBalance(String code, String account) throws Exception;
    void subscribe(Long lastBlock, BlockCallback callback, BlockReliability blockReliability) throws Exception;
}
