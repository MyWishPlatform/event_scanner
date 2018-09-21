package io.mywish.scanner.services;

import io.mywish.blockchain.WrapperBlock;
import io.mywish.blockchain.WrapperNetwork;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;

@Slf4j
public abstract class Scanner {
    protected static final long INFO_INTERVAL = 60000;
    protected static final long WARN_INTERVAL = 120000;

    protected final WrapperNetwork network;
    protected final LastBlockPersister lastBlockPersister;

    private Thread workerThread;

    protected Long nextBlockNo;
    protected long lastBlockIncrementTimestamp;

    @Autowired
    protected EventPublisher eventPublisher;

    abstract protected void processBlock(WrapperBlock block);

    public Scanner(WrapperNetwork network, LastBlockPersister lastBlockPersister) {
        this.network = network;
        this.lastBlockPersister = lastBlockPersister;
    }

    protected void setWorker(Runnable worker) {
        this.workerThread = new Thread(worker);
    }

	@EventListener
	private void onApplicationLoaded(ContextRefreshedEvent event) {
		workerThread.start();
		log.info("Subscribed to {} new block event.", network.getType());
	}

	@EventListener
	private void onApplicationClosed(ContextClosedEvent event) {
        log.info("Application closed.");
        close();
    }

    protected abstract void open() throws Exception;
    protected abstract void close();
}
