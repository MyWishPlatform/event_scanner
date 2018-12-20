package io.mywish.troncli4j;

import io.mywish.troncli4j.model.response.BlockResponse;
import io.mywish.troncli4j.model.response.NodeInfoResponse;

public interface TronClient {
    NodeInfoResponse getNodeInfo() throws Exception;

    BlockResponse getBlock(String id) throws Exception;

    BlockResponse getBlock(Long number) throws Exception;
//    BalanceResponse getBalance(String code, String account) throws Exception;
}
