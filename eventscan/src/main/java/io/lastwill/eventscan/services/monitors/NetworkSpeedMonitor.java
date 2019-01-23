package io.lastwill.eventscan.services.monitors;

import io.lastwill.eventscan.model.NetworkType;
import io.mywish.scanner.model.NewBlockEvent;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Component
public class NetworkSpeedMonitor {
    private final Map<NetworkType, List<Block>> blocks = new ConcurrentHashMap<>();

    @Value("${io.lastwill.eventscan.network-speed.interval}")
    private int interval;

    @EventListener
    protected void onNewBlock(NewBlockEvent event) {
        NetworkType networkType = event.getNetworkType();
        Instant now = Instant.now();
        blocks.putIfAbsent(networkType, Collections.synchronizedList(new ArrayList<>()));
        blocks.computeIfPresent(networkType, (networkType1, blocks) -> {
            blocks.add(new Block(now));
            return blocks;
        });
        cleanOldBlocks();
    }

    @Scheduled(fixedDelayString = "${io.lastwill.eventscan.network-speed.interval}",
            initialDelayString = "${io.lastwill.eventscan.network-speed.interval}")
    protected void cleanOldBlocks() {
        Instant now = Instant.now();
        blocks.values()
                .forEach(blocks -> blocks.removeIf(
                        block -> block.receivedTime.plusMillis(interval).isBefore(now)));
    }

    public double getSpeed(NetworkType networkType, TimeUnit timeUnit) {
        Instant now = Instant.now();
        long count = blocks.get(networkType)
                .stream()
                .filter(block -> block.receivedTime.plusMillis(interval).isAfter(now))
                .count();
        long millisInUnit = timeUnit.toMillis(1);
        return (double) count * millisInUnit / interval;
    }

    @Getter
    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Block {
        private final Instant receivedTime;
    }
}
