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
public class ProductWallet extends ProductCheckable {
    @Override
    public int getContractType() {
        return 1;
    }

    @Override
    public BigInteger getCheckGasLimit() {
        return BigInteger.valueOf(200000);
    }
}
