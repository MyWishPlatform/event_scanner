package io.lastwill.eventscan.model;

import lombok.Getter;

import javax.persistence.*;
import java.math.BigInteger;

@Entity
@Table(name = "contracts_contractdetailswavessto")
@PrimaryKeyJoinColumn(name = "contract_id")
@DiscriminatorValue("21")
@Getter
public class ProductStoWaves extends Product {
//    @Column(name = "stop_date")
    private String assetId;

    @Override
    public int getContractType() {
        return 21;
    }

    @Override
    public BigInteger getCheckGasLimit() {
        return BigInteger.ZERO;
    }

//    @Override
//    public Instant getCheckDate() {
//        return Instant.ofEpochSecond(finishTimestamp);
//    }
}
