package io.lastwill.eventscan.messages;

import lombok.Getter;

@Getter
public class InvestmentPoolSetupNotify extends NotifyContract {
    private final String investmentAddress;
    private final String tokenAddress;

    public InvestmentPoolSetupNotify(int contractId, String txHash, String investmentAddress, String tokenAddress) {
        super(contractId, PaymentStatus.COMMITTED, txHash);
        this.investmentAddress = investmentAddress;
        this.tokenAddress = tokenAddress;
    }

    @Override
    public String getType() {
        return "investmentPoolSetup";
    }
}
