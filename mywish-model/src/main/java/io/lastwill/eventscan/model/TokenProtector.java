package io.lastwill.eventscan.model;

import lombok.Getter;

import javax.persistence.*;
import java.math.BigInteger;

@Entity
@Table(name = "contracts_contractdetailstokenprotector")
@PrimaryKeyJoinColumn(name = "contract_id")
@DiscriminatorValue("23")
@Getter
public class TokenProtector extends Product {
    @Getter
    @Column(name = "owner_address")
    private String ownerAddress;

    @Getter
    @Column(name = "reserve_address")
    private String reserveAddress;

    @Getter
    @Column(name = "end_timestamp")
    private BigInteger endTimestamp;

    @Getter
    @Column(name = "email")
    private String email;

    @Getter
    @Column(name = "temp_directory")
    private String tempDirectory;

    @Override
    public int getContractType() {
        return 23;
    }

    @Override
    public BigInteger getCheckGasLimit() {
        return BigInteger.valueOf(200000);
    }
}
