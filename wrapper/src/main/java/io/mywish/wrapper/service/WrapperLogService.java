package io.mywish.wrapper.service;

import io.mywish.wrapper.WrapperLog;

public interface WrapperLogService<T> {
    WrapperLog build(T source);
}
