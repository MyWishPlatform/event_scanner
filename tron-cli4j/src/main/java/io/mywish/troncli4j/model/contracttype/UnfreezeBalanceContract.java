package io.mywish.troncli4j.model.contracttype;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class UnfreezeBalanceContract extends ContractType {
    private final ResourceCode resource;
}
