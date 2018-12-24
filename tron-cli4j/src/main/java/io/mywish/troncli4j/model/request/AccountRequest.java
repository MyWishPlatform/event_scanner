package io.mywish.troncli4j.model.request;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class AccountRequest extends Request {
    private final String address;
}
