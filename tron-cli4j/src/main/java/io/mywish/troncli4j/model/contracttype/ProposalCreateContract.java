package io.mywish.troncli4j.model.contracttype;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Map;

@Getter
@RequiredArgsConstructor
public class ProposalCreateContract extends ContractType {
    private Map<Long, Long> parameters;
}
