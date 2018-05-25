package io.mywish.wrapper.service;

import io.mywish.wrapper.WrapperBlock;

public interface WrapperBlockService<T> {
    WrapperBlock build(T source);
}
