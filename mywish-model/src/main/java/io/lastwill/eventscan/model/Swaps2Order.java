package io.lastwill.eventscan.model;

import lombok.Getter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;

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

    @Column(name = "public")
    private Boolean publicStatus;

    @Column(name = "base_address")
    private String baseAddress;

    @Column(name = "quote_address")
    private String quoteAddress;

    @Column(name = "quote_limit")
    private BigDecimal quoteLimit;

    @Column(name = "base_limit")
    private BigDecimal baseLimit;

    @Column(name = "unique_link")
    private String uniqueLink;
}
