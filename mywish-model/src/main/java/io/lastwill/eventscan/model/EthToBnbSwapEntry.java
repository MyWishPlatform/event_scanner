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

    private String ethTxHash;

    @Setter
    private String bnbTxHash;

    public EthToBnbSwapEntry(EthToBnbLinkEntry linkEntry, BigInteger amount, String ethTxHash) {
        this.linkEntry = linkEntry;
        this.amount = amount;
        this.ethTxHash = ethTxHash;
    }
}
