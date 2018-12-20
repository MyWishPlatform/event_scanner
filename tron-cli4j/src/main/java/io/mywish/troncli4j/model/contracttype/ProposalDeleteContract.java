package io.mywish.troncli4j.model.contracttype;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ProposalDeleteContract extends ContractType {
    @JsonProperty("proposal_id")
    private final Long proposalId;
}
