package io.lastwill.eventscan.model;

import lombok.Getter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "swaps_common_orderbookswaps")
@Getter
public class Swaps2Order {
    @Id
    private Integer id;

    @Column(name = "memo_contract")
    private String orderId;

    @Column(name = "user_id")
    private Integer user;

    @Column(name = "name")
    private String name;
}
