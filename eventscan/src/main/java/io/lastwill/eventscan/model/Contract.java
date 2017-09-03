package io.lastwill.eventscan.model;

import lombok.Getter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "contracts_contract")
@Getter
public class Contract {
    @Id
    private Integer id;
    private String address;
    private String ownerAddress;
    private String userAddress;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ContractState state;
    @Column(name = "activeTo", nullable = false)
    private LocalDateTime activeUntil;
    @Column(nullable = false)
    private int checkInterval;
}
