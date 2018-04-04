package io.lastwill.eventscan.model;

import lombok.Getter;

import javax.persistence.*;

@Entity
@Getter
@Table(name = "deploy_deployaddress")
public class AddressLock {
    @Id
    private Integer id;
    @ManyToOne
    @JoinColumn(name = "network_id")
    private Network network;
    private String address;
    private Integer lockedBy;
}
