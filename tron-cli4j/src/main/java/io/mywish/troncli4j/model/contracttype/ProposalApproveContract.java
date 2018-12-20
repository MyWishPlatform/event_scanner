package io.mywish.troncli4j.model.contracttype;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ProposalApproveContract extends ContractType {
    @JsonProperty("proposal_id")
    private final Long proposalId;
    @JsonProperty("is_add_approval")
    private final Boolean isAddApproval;
}
