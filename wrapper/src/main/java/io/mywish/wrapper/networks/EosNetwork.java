package io.mywish.wrapper.networks;

import io.lastwill.eventscan.model.NetworkType;
import io.mywish.eoscli4j.EosClient;
import io.mywish.eoscli4j.model.Transaction;
import io.mywish.wrapper.WrapperBlock;
import io.mywish.wrapper.WrapperNetwork;
import io.mywish.wrapper.WrapperTransaction;
import io.mywish.wrapper.WrapperTransactionReceipt;
import io.mywish.wrapper.service.block.WrapperBlockEosService;
import io.mywish.wrapper.service.transaction.WrapperTransactionEosService;
import io.mywish.wrapper.service.transaction.receipt.WrapperTransactionReceiptEosService;
import io.mywish.wrapper.transaction.WrapperTransactionEos;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigInteger;
import java.util.List;

public class EosNetwork extends WrapperNetwork {
    private final EosClient eosClient;

    @Autowired
    private WrapperBlockEosService blockBuilder;

    @Autowired
    private WrapperTransactionReceiptEosService transactionReceiptBuilder;

    @Autowired
    private WrapperTransactionEosService transactionBuilder;

    public EosNetwork(NetworkType type, EosClient eosClient) {
        super(type);
        this.eosClient = eosClient;
    }

    @Override
    public Long getLastBlock() throws Exception {
        return eosClient.getChainInfo().getHeadBlockNum();
    }

    @Override
    public BigInteger getBalance(String address, Long blockNo) throws Exception {
        return eosClient.getBalance("EOS", address).getValue();
    }

    @Override
    public WrapperBlock getBlock(String hash) throws Exception {
        return blockBuilder.build(eosClient.getBlock(hash));
    }

    @Override
    public WrapperBlock getBlock(Long number) throws Exception {
        return blockBuilder.build(eosClient.getBlock(number));
    }

    @Override
    public WrapperTransactionReceipt getTxReceipt(WrapperTransaction transaction) throws Exception {
        return transactionReceiptBuilder.build(((WrapperTransactionEos)transaction).getNativeTransaction());
    }

    @Override
    public boolean isPendingTransactionsSupported() {
        return false;
    }

    @Override
    public List<WrapperTransaction> fetchPendingTransactions() throws Exception {
        throw new Exception("Method not supported");
    }

    public interface EosBlockCallback {
        void callback(WrapperBlock block);
    }

    public void subscribe(Long lastBlock, EosBlockCallback callback) throws Exception {
        this.eosClient.subscribe(lastBlock, eosBlock -> {
            callback.callback(blockBuilder.build(eosBlock));
        });
    }
}
