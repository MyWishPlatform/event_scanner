package io.lastwill.eventscan.model;

import lombok.Getter;

import javax.persistence.*;
import java.math.BigInteger;
import java.time.Instant;
import java.time.ZonedDateTime;

@Entity
@Table(name = "contracts_contractdetailsdelayedpayment")
@PrimaryKeyJoinColumn(name = "contract_id")
@DiscriminatorValue("2")
public class ProductDelayedPayment extends Product implements ProductSingleCheck {
    @Column(name = "date")
    private ZonedDateTime delayedDate;

    @Override
    public int getContractType() {
        return 2;
    }

    @Override
    public BigInteger getCheckGasLimit() {
        // really it is finish gas price
        return BigInteger.valueOf(200000);
    }

    @Override
    public Instant getCheckDate() {
        return delayedDate.toInstant();
    }
}
