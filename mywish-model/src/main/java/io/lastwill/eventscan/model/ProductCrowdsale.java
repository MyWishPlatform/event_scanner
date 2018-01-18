package io.lastwill.eventscan.model;

import javax.persistence.*;
import java.math.BigInteger;
import java.time.Instant;
import java.time.ZonedDateTime;

@Entity
@Table(name = "contracts_contractdetailsico")
@PrimaryKeyJoinColumn(name = "contract_id")
@DiscriminatorValue("4")
public class ProductCrowdsale extends Product implements ProductSingleCheck {
    @Column(name = "stop_date")
    private int finishTimestamp;

    @Override
    public int getContractType() {
        return 4;
    }

    @Override
    public BigInteger getCheckGasLimit() {
        // really it is finish gas price
        return BigInteger.valueOf(200000);
    }

    @Override
    public Instant getCheckDate() {
        return Instant.ofEpochSecond(finishTimestamp);
    }
}
