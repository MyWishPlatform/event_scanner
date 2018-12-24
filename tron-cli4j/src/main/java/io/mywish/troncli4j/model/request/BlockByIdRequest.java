package io.mywish.troncli4j.model.request;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class BlockByIdRequest extends Request {
    private final String value;
}
