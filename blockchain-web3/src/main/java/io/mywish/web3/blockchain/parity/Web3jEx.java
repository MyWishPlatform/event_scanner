package io.mywish.web3.blockchain.parity;

import io.mywish.web3.blockchain.parity.model.GetPendingTransaction;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.Web3jService;
import org.web3j.protocol.core.Request;

public interface Web3jEx extends Web3j {
    Request<?, GetPendingTransaction> parityGetPendingTransactions();

    /**
     * Construct a new Web3j instance.
     *
     * @param web3jService web3j service instance - i.e. HTTP or IPC
     * @return new Web3j instance
     */
    static Web3jEx build(Web3jService web3jService) {
        return new ParityJsonRpc(web3jService);
    }
}
