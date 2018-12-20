package io.mywish.troncli4j;

import io.mywish.troncli4j.model.response.BlockResponse;

public interface BlockCallback {
    boolean callback(BlockResponse block);
}
