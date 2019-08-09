package io.lastwill.eventscan.model;

import lombok.Getter;

import javax.persistence.*;

@Entity
@Table(name = "contracts_contractdetailseostokensa")
@PrimaryKeyJoinColumn(name = "contract_id")
@DiscriminatorValue("14")
@Getter
public class ProductTokenExtEos extends ProductTokenCommon {
    @Column(name = "token_short_name")
    private String symbol;

    @Override
    public int getContractType() {
        return 14;
    }
}
