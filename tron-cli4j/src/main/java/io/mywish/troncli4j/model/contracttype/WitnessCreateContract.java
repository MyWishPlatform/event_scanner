package io.mywish.troncli4j.model.contracttype;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class WitnessCreateContract extends ContractType {
    private final String url;
}
