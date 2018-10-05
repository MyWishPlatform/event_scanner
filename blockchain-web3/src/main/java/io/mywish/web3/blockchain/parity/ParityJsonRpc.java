package io.mywish.web3.blockchain.parity;

import io.mywish.web3.blockchain.parity.model.GetPendingTransaction;
import org.web3j.protocol.Web3jService;
import org.web3j.protocol.core.JsonRpc2_0Web3j;
import org.web3j.protocol.core.Request;

import java.util.Collections;
import java.util.concurrent.ScheduledExecutorService;

public class ParityJsonRpc extends JsonRpc2_0Web3j implements Web3jEx {
    public ParityJsonRpc(Web3jService web3jService) {
        super(web3jService);
    }

    public ParityJsonRpc(Web3jService web3jService, long pollingInterval, ScheduledExecutorService scheduledExecutorService) {
        super(web3jService, pollingInterval, scheduledExecutorService);
    }

    @Override
    public Request<?, GetPendingTransaction> parityGetPendingTransactions() {
        return new Request<>(
                "parity_pendingTransactions",
                Collections.emptyList(),
                web3jService,
                GetPendingTransaction.class
        );
    }
}
