package io.lastwill.eventscan.services.monitors;

import io.lastwill.eventscan.model.NetworkType;
import io.mywish.scanner.model.NewBlockEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

@Component
public class NetworkSpeedMonitor {
    private final Map<NetworkType, Long> blockCounts = new ConcurrentHashMap<>();
    private final Map<NetworkType, Long> speeds = new ConcurrentHashMap<>();
    private final AtomicReference<Instant> now = new AtomicReference<>();

    @EventListener
    protected void onNewBlock(NewBlockEvent event) {
        NetworkType networkType = event.getNetworkType();
        blockCounts.compute(networkType,
                (network, oldCount) -> oldCount == null ? 1L : oldCount + 1);
    }

    @Scheduled(fixedDelayString = "${io.lastwill.eventscan.network-speed.interval}", initialDelayString = "${io.lastwill.eventscan.network-speed.interval}")
    protected void calculateSpeeds() {
        now.set(Instant.now());
        blockCounts.keySet()
                .forEach(networkType -> {
                    Long count = blockCounts.get(networkType);
                    speeds.put(networkType, count);
                    blockCounts.put(networkType, 0L);
                });
    }

    public Map<NetworkType, Long> getSpeeds() {
        return Collections.unmodifiableMap(speeds);
    }

    public Instant getNow() {
        return now.get();
    }
}
