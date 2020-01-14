package io.lastwill.eventscan.messages;


import io.lastwill.eventscan.events.model.contract.tokenProtector.SelfdestructionEvent;
import lombok.Getter;
import lombok.ToString;

@ToString(callSuper = true)
@Getter
public class SelfdestructionNotify extends NotifyContract {
    private final boolean status;

    public SelfdestructionNotify(int contractId, PaymentStatus status, String txHash, SelfdestructionEvent contractEvent) {
        super(contractId, status, txHash);
        this.status = contractEvent.isStatus();
    }

    @Override
    public String getType() {
        return "SelfdestructionEvent";
    }
}
