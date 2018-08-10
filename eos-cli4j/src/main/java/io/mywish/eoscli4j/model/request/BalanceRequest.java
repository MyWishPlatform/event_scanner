package io.mywish.eoscli4j.model.request;

import lombok.Getter;

@Getter
public class BalanceRequest extends Request {
    private final String code;
    private final String account;

    public BalanceRequest(String code, String account) {
        this.code = code;
        this.account = account;
    }
}
