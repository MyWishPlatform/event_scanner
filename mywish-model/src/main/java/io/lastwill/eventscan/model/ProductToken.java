package io.lastwill.eventscan.model;

import lombok.Getter;

import javax.persistence.*;
import java.math.BigInteger;
import java.time.Instant;

@Entity
@Table(name = "contracts_contractdetailstoken")
@PrimaryKeyJoinColumn(name = "contract_id")
@DiscriminatorValue("5")
@Getter
public class ProductToken extends ProductTokenCommon {
    @Column(name = "token_name")
    private String name;

    @Column(name = "token_short_name")
    private String symbol;

    @Override
    public int getContractType() {
        return 5;
    }
}
