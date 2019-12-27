package io.lastwill.eventscan.messages;

import io.lastwill.eventscan.events.model.contract.tokenProtector.TokensToSaveEvent;
import lombok.Getter;
import lombok.ToString;

@ToString(callSuper = true)
@Getter
public class TokensToSaveNotify extends NotifyContract {
    private final String tokenContract;

    public TokensToSaveNotify(int contractId, PaymentStatus status, String txHash, TokensToSaveEvent contractEvent) {
        super(contractId, status, txHash);
        this.tokenContract = contractEvent.getTokenContract();
    }

    @Override
    public String getType() {
        return "TokenProtectorTokensToSave";
    }
}
