package io.lastwill.eventscan.services.monitors;

import io.lastwill.eventscan.events.model.PendingTransactionAddedEvent;
import io.lastwill.eventscan.events.model.utility.NetworkStuckEvent;
import io.lastwill.eventscan.events.model.utility.PendingStuckEvent;
import io.lastwill.eventscan.model.NetworkType;
import io.mywish.scanner.model.NewBlockEvent;
import io.mywish.scanner.model.NewPendingTransactionsEvent;
import io.mywish.scanner.services.EventPublisher;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class NetworkStuckMonitor {
    private final ConcurrentHashMap<NetworkType, LastEvent> lastBlockEvents = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<NetworkType, LastEvent> lastPendingTxEvents = new ConcurrentHashMap<>();
    @Value("${io.lastwill.eventscan.network-stuck.interval.btc}")
    private long btcInterval;
    @Value("${io.lastwill.eventscan.network-stuck.interval.eth}")
    private long ethInterval;
    @Value("${io.lastwill.eventscan.network-stuck.interval.pending}")
    private long pendingInterval;

    @Autowired
    private EventPublisher eventPublisher;

    @EventListener
    private void newBlockEvent(NewBlockEvent event) {
        lastBlockEvents.put(
                event.getNetworkType(),
                new LastEvent(
                        LocalDateTime.now(ZoneOffset.UTC),
                        Instant.ofEpochSecond(event.getBlock().getTimestamp()),
                        event.getBlock().getNumber()
                )
        );
    }

    @EventListener
    private void newPendingTx(NewPendingTransactionsEvent event) {
        lastPendingTxEvents.put(
                event.getNetworkType(),
                new LastEvent(
                        LocalDateTime.now(ZoneOffset.UTC),
                        Instant.now(),
                        event.getPendingTransactions().size()
                )
        );
    }

    public Map<NetworkType, LastEvent> getLastBlockEvents() {
        return Collections.unmodifiableMap(this.lastBlockEvents);
    }

    public Map<NetworkType, LastEvent> getLastPendingTxEvents() {
        return Collections.unmodifiableMap(this.lastPendingTxEvents);
    }

    @Scheduled(fixedDelayString = "${io.lastwill.eventscan.network-stuck.interval.eth}", initialDelayString = "${io.lastwill.eventscan.network-stuck.interval.eth}")
    protected void checkEth() {
        final LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);
        lastBlockEvents.keySet()
                .stream()
                .filter(networkType -> networkType != NetworkType.BTC_MAINNET && networkType != NetworkType.BTC_TESTNET_3)
                .forEach(networkType -> {
                    LastEvent lastEvent = lastBlockEvents.get(networkType);
                    // last block + interval is in future
                    if (lastEvent.receivedTime.plusSeconds(ethInterval / 1000).isAfter(now)) {
                        return;
                    }

                    eventPublisher.publish(
                            new NetworkStuckEvent(
                                    networkType,
                                    lastEvent.receivedTime,
                                    lastEvent.timestamp,
                                    lastEvent.blockNo
                            )
                    );
                });
    }

    @Scheduled(fixedDelayString = "${io.lastwill.eventscan.network-stuck.interval.btc}", initialDelayString = "${io.lastwill.eventscan.network-stuck.interval.btc}")
    protected void checkBtc() {
        final LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);
        lastBlockEvents.keySet()
                .stream()
                .filter(networkType -> networkType == NetworkType.BTC_MAINNET || networkType == NetworkType.BTC_TESTNET_3)
                .forEach(networkType -> {
                    LastEvent lastEvent = lastBlockEvents.get(networkType);
                    // last block + interval is in future
                    if (lastEvent.receivedTime.plusSeconds(btcInterval / 1000).isAfter(now)) {
                        return;
                    }

                    eventPublisher.publish(
                            new NetworkStuckEvent(
                                    networkType,
                                    lastEvent.receivedTime,
                                    lastEvent.timestamp,
                                    lastEvent.blockNo
                            )
                    );
                });
    }

    @Scheduled(fixedDelayString = "${io.lastwill.eventscan.network-stuck.interval.pending}", initialDelayString = "${io.lastwill.eventscan.network-stuck.interval.pending}")
    protected void checkPending() {
        final LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);
        lastPendingTxEvents.keySet()
                .stream()
//                .filter(networkType -> networkType == NetworkType.BTC_MAINNET || networkType == NetworkType.BTC_TESTNET_3)
                .forEach(networkType -> {
                    LastEvent lastEvent = lastPendingTxEvents.get(networkType);
                    // last block + interval is in future
                    if (lastEvent.receivedTime.plusSeconds(pendingInterval / 1000).isAfter(now)) {
                        return;
                    }

                    eventPublisher.publish(
                            new PendingStuckEvent(
                                    networkType,
                                    lastEvent.receivedTime,
                                    (int) lastEvent.blockNo
                            )
                    );
                });
    }

    @Getter
    public static class LastEvent {
        private final LocalDateTime receivedTime;
        private final Instant timestamp;
        private final long blockNo;

        private LastEvent(LocalDateTime receivedTime, Instant timestamp, long blockNo) {
            this.receivedTime = receivedTime;
            this.timestamp = timestamp;
            this.blockNo = blockNo;
        }
    }
}
