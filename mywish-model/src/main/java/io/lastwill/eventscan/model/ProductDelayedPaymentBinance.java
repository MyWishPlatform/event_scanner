package io.lastwill.eventscan.model;

import javax.persistence.*;
import java.math.BigInteger;
import java.time.Instant;
import java.time.ZonedDateTime;

@Entity
@Table(name = "contracts_contractdetailsbinancedelayedpayment")
@PrimaryKeyJoinColumn(name = "contract_id")
@DiscriminatorValue("26")
public class ProductDelayedPaymentBinance extends Product implements ProductSingleCheck {
    @Column(name = "date")
    private ZonedDateTime delayedDate;

    @Override
    public int getContractType() {
        return 26;
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
