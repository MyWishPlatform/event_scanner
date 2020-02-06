package io.mywish.btc.blockchain.helper;

import org.bitcoinj.params.MainNetParams;

public class DucatusNetworkParams extends MainNetParams {
    public DucatusNetworkParams() {
        dumpedPrivateKeyHeader = 177;
        bip32HeaderPub = 0x019da462; // The 4 byte header that serializes in base58 to "xpub".
        bip32HeaderPriv = 0x019d9cfe; // The 4 byte header that serializes in base58 to "xprv"
        packetMagic = 0xf9beb4d9;
        addressHeader = 49;
        p2shHeader = 51;
    }
}