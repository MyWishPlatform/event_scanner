package io.lastwill.eventscan.messages;

import io.lastwill.eventscan.events.model.contract.erc20.ApprovalEvent;
import lombok.Getter;
import lombok.ToString;
import java.math.BigInteger;

@ToString(callSuper = true)
@Getter
public class ApproveTokenProtectorNotify extends NotifyContract {
    private final String tokenAddress;
    private final String owner;
    private final String spender;
    private final BigInteger tokens;

    public ApproveTokenProtectorNotify(int contractId, PaymentStatus status, String txHash, ApprovalEvent contractEvent) {
        super(contractId, status, txHash);
        this.owner = contractEvent.getOwner();
        this.spender = contractEvent.getSpender();
        this.tokens = contractEvent.getTokens();
        this.tokenAddress = contractEvent.getAddress();
    }

    @Override
    public String getType() {
        return "TokenProtectorApprove";
    }
}
