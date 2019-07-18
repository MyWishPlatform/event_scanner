package io.lastwill.eventscan.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "eth_bnb_swap_link_entry",
uniqueConstraints = {@UniqueConstraint(columnNames = {"symbol", "eth_address"})})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EthToBnbLinkEntry{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String symbol;

    @Column(nullable = false,
    name = "eth_address")
    private String ethAddress;

    @Column(nullable = false,
    name = "bnb_address")
    private String bnbAddress;

    public EthToBnbLinkEntry(String symbol, String ethAddress, String bnbAddress) {
        this.symbol = symbol;
        this.ethAddress = ethAddress;
        this.bnbAddress = bnbAddress;
    }
}
