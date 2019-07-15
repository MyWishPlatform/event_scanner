package io.lastwill.eventscan.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "eth_bnb_swap_link_entry")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EthToBnbLinkEntry{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String symbol;

    @Column(nullable = false)
    private String ethAddress;

    @Column(nullable = false)
    private String bnbAddress;

    public EthToBnbLinkEntry(String symbol, String ethAddress, String bnbAddress) {
        this.symbol = symbol;
        this.ethAddress = ethAddress;
        this.bnbAddress = bnbAddress;
    }
}
