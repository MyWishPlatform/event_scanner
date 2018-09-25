package io.mywish.blockchain.service;

import io.mywish.blockchain.WrapperLog;

public interface WrapperLogService<T> {
    WrapperLog build(T source);
}
