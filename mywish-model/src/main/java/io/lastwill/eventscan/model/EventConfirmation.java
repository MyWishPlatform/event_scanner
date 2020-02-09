package io.lastwill.eventscan.model;

import lombok.Getter;

import javax.persistence.*;


@Entity
@Table(name = "Event_Confirmation")
@Getter
public class EventConfirmation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tx_hash", nullable = false)
    private String hash;

    @Column(name = "block_number", nullable = false)
    private Long blockNumber;

    @Column(name = "network", nullable = false)
    private NetworkType network;

    protected EventConfirmation() {
    }

    public EventConfirmation(String hash, Long blockNumber, NetworkType network) {
        this.hash = hash;
        this.blockNumber = blockNumber;
        this.network = network;
    }
}
