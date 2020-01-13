package io.lastwill.eventscan.messages;

import io.lastwill.eventscan.events.model.contract.tokenProtector.TransactionInfoEvent;
import lombok.Getter;
import lombok.ToString;

import java.math.BigInteger;


@ToString(callSuper = true)
@Getter
public class TransactionInfoNotify extends NotifyContract {
    private final String tokenContract;
    private final BigInteger amount;

    public TransactionInfoNotify(int contractId, PaymentStatus status, String txHash, TransactionInfoEvent contractEvent) {
        super(contractId, status, txHash);
        this.tokenContract = contractEvent.getTokenContract();
        this.amount = contractEvent.getAmount();
    }

    @Override
    public String getType() {
        return "TokenProtectorTransactionInfo";
    }
}
