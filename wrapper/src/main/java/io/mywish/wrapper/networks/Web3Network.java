package io.mywish.wrapper.networks;

import io.mywish.wrapper.WrapperNetwork;
import io.mywish.wrapper.WrapperTransactionReceipt;
import io.lastwill.eventscan.model.NetworkType;
import io.mywish.wrapper.*;
import io.mywish.wrapper.service.block.WrapperBlockWeb3Service;
import io.mywish.wrapper.service.transaction.WrapperTransactionWeb3Service;
import io.mywish.wrapper.service.transaction.receipt.WrapperTransactionReceiptWeb3Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterNumber;

import javax.annotation.PostConstruct;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Web3Network extends WrapperNetwork {
    final private Web3j web3j;

    @Autowired
    private WrapperBlockWeb3Service blockBuilder;

    @Autowired
    private WrapperTransactionWeb3Service transactionBuilder;

    @Autowired
    private WrapperTransactionReceiptWeb3Service transactionReceiptBuilder;

    @Autowired
    private List<ContractEventBuilder<?>> builders = new ArrayList<>();

    private Map<String, ContractEventDefinition> definitionsBySignature = new HashMap<>();

    public Web3Network(NetworkType type, Web3j web3j) {
        super(type);
        this.web3j = web3j;
    }

    @PostConstruct
    private void init() throws Exception {
        for (ContractEventBuilder<?> eventBuilder : builders) {
            if (definitionsBySignature.containsKey(eventBuilder.getDefinition().getSignature())) {
                throw new Exception("Duplicate builder " + eventBuilder.getClass() + " with signature " + eventBuilder.getDefinition().getSignature());
            }
            definitionsBySignature.put(eventBuilder.getDefinition().getSignature(), eventBuilder.getDefinition());
        }
    }

    @Override
    public Long getLastBlock() throws Exception {
        return web3j.ethBlockNumber().send().getBlockNumber().longValue();
    }

    @Override
    public WrapperBlock getBlock(String hash) throws Exception {
        return blockBuilder.build(web3j.ethGetBlockByHash(hash, false).send().getBlock());
    }

    @Override
    public WrapperBlock getBlock(Long number) throws Exception {
        return blockBuilder.build(web3j.ethGetBlockByNumber(new DefaultBlockParameterNumber(number), true).send().getBlock());
    }

    @Override
    public WrapperTransaction getTransaction(String hash) throws Exception {
        return transactionBuilder.build(web3j.ethGetTransactionByHash(hash).send().getTransaction().get());
    }

    @Override
    public BigInteger getBalance(String address, Long blockNo) throws Exception {
        return web3j
                .ethGetBalance(address, new DefaultBlockParameterNumber(blockNo))
                .send()
                .getBalance();
    }

    @Override
    public WrapperTransactionReceipt getTxReceipt(WrapperTransaction transaction) throws Exception {
        return transactionReceiptBuilder.build(
                web3j
                        .ethGetTransactionReceipt(transaction.getHash())
                        .send()
                        .getResult(),
                definitionsBySignature
        );
    }
}
