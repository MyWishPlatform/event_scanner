package io.lastwill.eventscan.model;

import lombok.Getter;

import javax.persistence.*;
import java.math.BigInteger;

@Entity
@Table(name = "contracts_contractdetailsswaps2")
@PrimaryKeyJoinColumn(name = "contract_id")
@DiscriminatorValue("21")
@Getter
public class ProductSwaps2 extends Product {
    @Column(name = "memo_contract")
    private String orderId;

    @Override
    public int getContractType() {
        return 21;
    }

    @Override
    public BigInteger getCheckGasLimit() {
        return BigInteger.ZERO;
    }
}
