package io.lastwill.eventscan.model;

import lombok.Getter;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

@Entity
@Table(name = "contracts_contractdetailstronlostkey")
@PrimaryKeyJoinColumn(name = "contract_id")
@DiscriminatorValue("18")
@Getter
public class ProductLostKeyTronTokens extends ProductLostKeyWallet {
    @Override
    public int getContractType() {
        return 18;
    }
}
