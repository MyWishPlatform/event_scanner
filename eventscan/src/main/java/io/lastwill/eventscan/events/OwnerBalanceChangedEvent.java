package io.lastwill.eventscan.events;

import io.lastwill.eventscan.model.Product;
import lombok.Getter;
import org.web3j.protocol.core.methods.response.EthBlock;

import java.math.BigInteger;

@Getter
public class OwnerBalanceChangedEvent extends BalanceChangedEvent {
    private final Product product;
    public OwnerBalanceChangedEvent(EthBlock.Block block, Product product, BigInteger amount, BigInteger balance) {
        super(block, product.getOwnerAddress(), amount, balance);
        this.product = product;
    }
}
