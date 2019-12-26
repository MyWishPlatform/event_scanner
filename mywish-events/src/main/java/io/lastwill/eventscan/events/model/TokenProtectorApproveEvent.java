package io.lastwill.eventscan.events.model;

import io.lastwill.eventscan.events.model.contract.erc20.ApprovalEvent;
import io.lastwill.eventscan.model.NetworkType;
import io.mywish.blockchain.WrapperTransaction;

public class TokenProtectorApproveEvent extends BaseEvent {
    private final WrapperTransaction transaction;
    private final ApprovalEvent approvalEvent;
    private final Integer contractId;

    public TokenProtectorApproveEvent(NetworkType networkType, WrapperTransaction transaction,
                                      ApprovalEvent approvalEvent, Integer contractId) {
        super(networkType);
        this.transaction = transaction;
        this.approvalEvent = approvalEvent;
        this.contractId = contractId;
    }
}
