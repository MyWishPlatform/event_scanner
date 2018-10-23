package io.mywish.airdrop.services;

import io.topiacoin.eosrpcadapter.EOSRPCAdapter;
import io.topiacoin.eosrpcadapter.JavaWallet;
import io.topiacoin.eosrpcadapter.Wallet;

import java.net.URL;

public class EosAdapter extends EOSRPCAdapter {
    private Wallet wallet;

    public EosAdapter(URL nodeURL) {
        super(nodeURL, null);
        wallet = new JavaWallet();
    }

    /**
     * Returns an instance of the Wallet class that can be used to interact with the wallet.
     *
     * @return An instance of the Wallet class
     */
    public Wallet wallet() {
        return wallet;
    }
}
