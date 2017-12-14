package io.lastwill.eventscan.model;

import lombok.Getter;

import javax.persistence.*;
import java.math.BigInteger;

@Entity
@Table(name = "contracts_contract")
@Getter
public class Product {
    @Id
    private Integer id;
    private String ownerAddress;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ContractState state;
    private BigInteger balance;
    @Column(nullable = false)
    private BigInteger cost;
}
