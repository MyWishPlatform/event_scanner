package io.mywish.blockchain.service;

import io.mywish.blockchain.WrapperOutput;

public interface WrapperOutputService<T> {
    WrapperOutput build(T source);
}
