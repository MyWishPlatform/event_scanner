package io.lastwill.eventscan.messages;

import io.lastwill.eventscan.model.AirdropEntry;
import lombok.Getter;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;

public class AirdropNotify extends NotifyContract {
    @Getter
    private final List<AirdropEntry> airdroppedAddresses;

    public AirdropNotify(int contractId, PaymentStatus status, String txHash, List<AirdropEntry> airdroppedAddresses) {
        super(contractId, status, txHash);
        this.airdroppedAddresses = airdroppedAddresses;
    }

    @Override
    public String getType() {
        return "airdrop";
    }
}
