package io.mywish.troncli4j;

import io.mywish.troncli4j.model.EventResult;
import io.mywish.troncli4j.model.response.AccountResponse;
import io.mywish.troncli4j.model.response.BlockResponse;
import io.mywish.troncli4j.model.response.NodeInfoResponse;

import java.util.List;

public interface TronClient {
    NodeInfoResponse getNodeInfo() throws Exception;

    BlockResponse getBlock(String id) throws Exception;

    BlockResponse getBlock(Long number) throws Exception;

    List<EventResult> getEventResult(String txId) throws Exception;

    AccountResponse getAccount(String hexAddress) throws Exception;
}
