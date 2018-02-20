package io.lastwill.eventscan.model;

import lombok.Getter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Getter
@Table(name = "deploy_deployaddress")
public class AddressLock {
    @Id
    private Integer id;
    private String address;
    private Integer lockedBy;
}
