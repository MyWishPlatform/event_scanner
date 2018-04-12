package io.mywish.scanner.services;

import org.bitcoinj.core.*;
import org.bitcoinj.core.listeners.PeerDataEventListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Nullable;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.List;

//@Component
public class BtcDirectScanner {
    @Autowired
    private PeerGroup peerGroup;

    private final PeerDataEventListener peerDataEventListener = new PeerDataEventListener() {
        @Override
        public void onBlocksDownloaded(Peer peer, Block block, @Nullable FilteredBlock filteredBlock, int blocksLeft) {

        }

        @Override
        public void onChainDownloadStarted(Peer peer, int blocksLeft) {

        }

        @Nullable
        @Override
        public List<Message> getData(Peer peer, GetDataMessage m) {
            return null;
        }

        @Override
        public Message onPreMessageReceived(Peer peer, Message m) {
            return null;
        }
    };

    @PostConstruct
    protected void init() {
//        peerGroup.addPeerDiscovery();
        peerGroup.setBloomFilteringEnabled(false);
        peerGroup.startBlockChainDownload(peerDataEventListener);
        peerGroup.startAsync();
    }

    @PreDestroy
    protected void close() {
        peerGroup.stop();
    }
}
