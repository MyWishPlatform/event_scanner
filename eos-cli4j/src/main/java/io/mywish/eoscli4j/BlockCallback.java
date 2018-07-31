package io.mywish.eoscli4j;

import io.mywish.eoscli4j.model.response.BlockResponse;

public interface BlockCallback {
    void callback(BlockResponse block);
}
