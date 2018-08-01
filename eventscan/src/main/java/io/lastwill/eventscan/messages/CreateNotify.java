package io.lastwill.eventscan.messages;


import lombok.Getter;

@Getter
public class CreateNotify extends NotifyContract {
    private String issuer;
    private String supply;

    public CreateNotify(int contractId, String txHash, String issuer, String supply) {
        super(contractId, PaymentStatus.COMMITTED, txHash);
        this.issuer = issuer;
        this.supply = supply;
    }

    @Override
    public String getType() {
        return "create";
    }
}
