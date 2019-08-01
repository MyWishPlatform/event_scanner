package io.lastwill.eventscan.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigInteger;

@Entity
@Table(name = "eth_bnb_swap_swap_entry")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EthToBnbSwapEntry  {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private EthToBnbLinkEntry linkEntry;

    private BigInteger amount;

    @Column(name = "eth_tx_hash")
    private String ethTxHash;

    @Setter
    @Column(name = "bnb_tx_hash")
    private String bnbTxHash;

    @Setter
    @Column(name = "transfer_status")
    @Enumerated(EnumType.STRING)
    private TransferStatus transferStatus;


    public EthToBnbSwapEntry(EthToBnbLinkEntry linkEntry, BigInteger amount, String ethTxHash) {
        this.linkEntry = linkEntry;
        this.amount = amount;
        this.ethTxHash = ethTxHash;
    }
}
