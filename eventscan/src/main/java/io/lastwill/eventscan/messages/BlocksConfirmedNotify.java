package io.lastwill.eventscan.messages;

import io.lastwill.eventscan.events.model.TransactionConfirmationEvent;
import io.lastwill.eventscan.events.model.contract.tokenProtector.SelfdestructionEvent;
import io.mywish.blockchain.ContractEventDefinition;
import lombok.Getter;
import lombok.ToString;

@ToString(callSuper = true)
@Getter
public class BlocksConfirmedNotify extends NotifyContract {
    private final long blocksConfirmed;

  //  private static ContractEventDefinition definition = new ContractEventDefinition("TransactionConfirmation");

    public BlocksConfirmedNotify(int contractId, PaymentStatus status, String txHash, Long blocksConfirmed, TransactionConfirmationEvent contractEvent) {
        super(contractId, status, txHash);
        this.blocksConfirmed = blocksConfirmed;
    }

    @Override
    public String getType() {
        return "BlocksConfirmed";
    }
}
