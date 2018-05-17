package io.mywish.scanner.networks;

import com.sun.istack.internal.NotNull;
import io.mywish.scanner.Network;
import io.mywish.scanner.WrapperTransactionReceipt;
import io.mywish.scanner.model.NetworkType;
import lombok.Getter;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterNumber;

import java.math.BigInteger;

public class Web3Network extends Network {
    @Getter
    final private Web3j web3j;

    public Web3Network(NetworkType type, Web3j web3j) {
        super(type);
        this.web3j = web3j;
    }

    @Override
    public Long getLastBlock() throws java.io.IOException {
        return web3j.ethBlockNumber().send().getBlockNumber().longValue();
    }

    @Override
    public BigInteger getBalance(String address, Long blockNo) throws java.io.IOException {
        return web3j
                .ethGetBalance(address, new DefaultBlockParameterNumber(blockNo))
                .send()
                .getBalance();
    }

    @Override
    public WrapperTransactionReceipt getTxReceipt(String hash) throws java.io.IOException {
        return new WrapperTransactionReceipt(web3j
                .ethGetTransactionReceipt(hash)
                .send()
                .getTransactionReceipt()
                .orElse(null)
        );
    }
}

