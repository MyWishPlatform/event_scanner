package io.lastwill.eventscan.model;

import lombok.Getter;

import javax.persistence.*;
import java.math.BigInteger;
import java.time.Instant;

@Entity
@Table(name = "contracts_contractdetailsbinanceico")
@PrimaryKeyJoinColumn(name = "contract_id")
@DiscriminatorValue("27")
@Getter
public class ProductCrowdsaleBinance extends ProductTokenCommon implements ProductSingleCheck {
    @Column(name = "token_short_name")
    private String symbol;

    @Column(name = "stop_date")
    private int finishTimestamp;
    @ManyToOne
    @JoinColumn(name = "eth_contract_crowdsale_id")
    private Contract crowdsaleContract;
    @ManyToOne
    @JoinColumn(name = "eth_contract_token_id")
    private Contract tokenContract;

    @Override
    public int getContractType() {
        return 27;
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
