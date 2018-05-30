package io.lastwill.eventscan.model;

import lombok.Getter;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import java.math.BigInteger;

@Getter
public abstract class ProductTokenCommon extends Product {
    @Override
    public BigInteger getCheckGasLimit() {
        // TODO: why it is required?
        return BigInteger.valueOf(200000);
    }
}
