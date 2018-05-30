package io.mywish.wrapper.service;

import io.mywish.wrapper.WrapperOutput;

public interface WrapperOutputService<T> {
    WrapperOutput build(T source);
}
