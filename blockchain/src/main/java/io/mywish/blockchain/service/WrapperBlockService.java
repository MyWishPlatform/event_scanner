package io.mywish.blockchain.service;

import io.mywish.blockchain.WrapperBlock;

public interface WrapperBlockService<T> {
    WrapperBlock build(T source);
}
