package io.lastwill.eventscan.events.model.contract.waves;

import lombok.Getter;

@Getter
public class AssetTransferEvent extends WavesContractEvent {
    protected final String assetId;
    protected final String sender;
    protected final String recipient;
    protected final long amount;
    protected final byte[] attachment;

    public AssetTransferEvent(String assetId, String sender, String recipient, long amount, byte[] attachment) {
        super(null);
        this.assetId = assetId;
        this.sender = sender;
        this.recipient = recipient;
        this.amount = amount;
        this.attachment = attachment;
    }
}
