package io.mywish.wrapper.service;

import io.mywish.wrapper.WrapperTransaction;

public interface WrapperTransactionService<T> {
    WrapperTransaction build(T source);
}
