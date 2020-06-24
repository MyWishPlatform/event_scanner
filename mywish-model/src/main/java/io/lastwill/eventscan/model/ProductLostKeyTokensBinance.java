package io.lastwill.eventscan.model;

import lombok.Getter;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

@Entity
@Table(name = "contracts_contractdetailsbinancelostkeytokens")
@PrimaryKeyJoinColumn(name = "contract_id")
@DiscriminatorValue("31")
@Getter
public class ProductLostKeyTokensBinance extends ProductLostKeyWalletBinance{
    @Override
    public int getContractType() {
        return 31;
    }
}
