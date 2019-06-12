package io.lastwill.eventscan.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "wish_bnb_swap_link_entry")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class WishToBnbLinkEntry {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String ethAddress;

    @Column(nullable = false)
    private String bnbAddress;

    public WishToBnbLinkEntry(String ethAddress, String bnbAddress) {
        this.ethAddress = ethAddress;
        this.bnbAddress = bnbAddress;
    }
}
