package io.mywish.troncli4j.model.request;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class BlockByNumRequest extends Request {
    private final Long num;
}
