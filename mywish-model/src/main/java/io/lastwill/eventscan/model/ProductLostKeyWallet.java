package io.lastwill.eventscan.model;

import lombok.Getter;

import javax.persistence.*;
import java.math.BigInteger;
import java.time.ZonedDateTime;

@Getter
@Entity
@PrimaryKeyJoinColumn(name = "contract_id")
@DiscriminatorValue("1")
@Table(name = "contracts_contractdetailslostkey")
public class ProductLostKeyWallet extends Product implements ProductCheckable {
    @Column(name = "check_interval")
    private Integer checkInterval;
    @Column(name = "active_to")
    private ZonedDateTime activeTo;
    @Column(name = "last_check")
    private ZonedDateTime lastCheck;
    @Column(name = "next_check")
    private ZonedDateTime nextCheck;

    @Override
    public int getContractType() {
        return 1;
    }

    @Override
    public BigInteger getCheckGasLimit() {
        return BigInteger.valueOf(200000);
    }
}
