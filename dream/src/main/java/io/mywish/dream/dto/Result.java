package io.mywish.dream.dto;

import lombok.Getter;

@Getter
public class Result {
    private final boolean status;
    private final Object data;
    private final String error = null;

    public Result(boolean status, Object data) {
        this.status = status;
        this.data = data;
    }
}
