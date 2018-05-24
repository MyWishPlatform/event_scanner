package io.mywish.wrapper.networks;

import io.mywish.neocli4j.NeoClient;
import io.mywish.wrapper.WrapperNetwork;
import io.mywish.wrapper.WrapperTransactionReceipt;
import io.lastwill.eventscan.model.NetworkType;
import io.mywish.wrapper.*;
import io.mywish.wrapper.block.WrapperBlockNeo;
import io.mywish.wrapper.log.WrapperLogNeo;
import io.mywish.wrapper.transaction.WrapperTransactionNeo;
import io.mywish.wrapper.transaction.receipt.WrapperTransactionReceiptNeo;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class NeoNetwork extends WrapperNetwork {
    final private NeoClient neoClient;

    @Autowired
    private List<ContractEventBuilder<?>> builders = new ArrayList<>();

    private Map<String, ContractEventDefinition> definitionsByName = new HashMap<>();

    public NeoNetwork(NetworkType type, NeoClient neoClient) {
        super(type);
        this.neoClient = neoClient;
    }

    @PostConstruct
    private void init() throws Exception {
        for (ContractEventBuilder<?> eventBuilder: builders) {
            if (definitionsByName.containsKey(eventBuilder.getDefinition().getName())) {
                throw new Exception("Duplicate builder " + eventBuilder.getClass() + " with signature " + eventBuilder.getDefinition().getName());
            }
            definitionsByName.put(eventBuilder.getDefinition().getName(), eventBuilder.getDefinition());
        }
    }

    @Override
    public Long getLastBlock() throws Exception {
        return (long)neoClient.getBlockCount();
    }

    @Override
    public WrapperBlock getBlock(String hash) throws Exception {
        return new WrapperBlockNeo(neoClient.getBlock(hash));
    }

    @Override
    public WrapperBlock getBlock(Long number) throws Exception {
        return new WrapperBlockNeo(neoClient.getBlock(number));
    }

    @Override
    public WrapperTransaction getTransaction(String hash) throws Exception {
        return new WrapperTransactionNeo(neoClient.getTransaction(hash, false));
    }
/*
    @Override
    public WrapperTransaction getTransaction(String hash) throws java.io.IOException {
        return new WrapperTransactionNeo(neoClient.getTransaction(hash, true));
    }
*/
    @Override
    public BigInteger getBalance(String address, Long blockNo) throws Exception {
        return neoClient.getBalance(address);
    }

    @Override
    public WrapperTransactionReceipt getTxReceipt(WrapperTransaction transaction) throws Exception {
        return new WrapperTransactionReceiptNeo(
                (WrapperTransactionNeo) transaction,
                neoClient.getEvents(transaction.getHash()).stream().map(event -> new WrapperLogNeo(event, definitionsByName.get(event.getName()))).collect(Collectors.toList())
        );
    }
}
