package io.lastwill.eventscan.model;

import lombok.Getter;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

@Entity
@Table(name = "contracts_contractdetailseostokensa")
@PrimaryKeyJoinColumn(name = "contract_id")
@DiscriminatorValue("14")
@Getter
public class ProductTokenExtEos extends ProductToken {
    @Override
    public int getContractType() {
        return 14;
    }
}
