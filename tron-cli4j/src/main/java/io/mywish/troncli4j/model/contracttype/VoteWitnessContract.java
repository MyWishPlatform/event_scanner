package io.mywish.troncli4j.model.contracttype;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Collections;
import java.util.List;

@RequiredArgsConstructor
public class VoteWitnessContract extends ContractType {
    private final List<Vote> votes;
    @Getter
    private final Boolean support;

    public List<Vote> getVotes() {
        if (votes == null) {
            return Collections.emptyList();
        }
        return votes;
    }

    @Getter
    @RequiredArgsConstructor
    public static class Vote {
        @JsonProperty("vote_address")
        private final String voteAddress;
        @JsonProperty("vote_count")
        private final Long voteCount;
    }
}
