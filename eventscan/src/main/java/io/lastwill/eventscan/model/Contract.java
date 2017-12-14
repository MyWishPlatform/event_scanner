package io.lastwill.eventscan.model;

import lombok.Getter;

import javax.persistence.*;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;

@Entity
@Table(name = "contracts_ethcontract")
@Getter
public class Contract {
    @Id
    private Integer id;
    private String address;
    // TODO: add convertors
//    @Column(name = "activeTo", nullable = false)
//    private OffsetDateTime activeUntil;
//    @Column(nullable = false)
//    private int checkInterval;
    @ManyToOne(optional = false)
    @JoinColumn(name = "contract_id")
    private Product product;
}
