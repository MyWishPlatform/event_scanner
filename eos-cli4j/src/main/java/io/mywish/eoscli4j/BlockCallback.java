package io.mywish.eoscli4j;

import io.mywish.eoscli4j.model.response.BlockResponse;

public interface BlockCallback {
    boolean callback(BlockResponse block);
}
