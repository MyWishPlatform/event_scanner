package io.lastwill.eventscan.model;

import lombok.Getter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Getter
@Table(name = "contracts_btckey4rsk")
public class BtcKey {
    @Id
    private Integer id;
    @Column(name = "btc_address")
    private String address;
}
