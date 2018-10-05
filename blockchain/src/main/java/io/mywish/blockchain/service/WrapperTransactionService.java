package io.mywish.blockchain.service;

import io.mywish.blockchain.WrapperTransaction;

public interface WrapperTransactionService<T> {
    WrapperTransaction build(T source);
}
