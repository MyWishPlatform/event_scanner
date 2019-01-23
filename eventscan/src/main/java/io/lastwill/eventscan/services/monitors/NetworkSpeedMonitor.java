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

import javax.annotation.PostConstruct;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

@Component
public class NetworkSpeedMonitor {
    private final Map<NetworkType, List<Block>> blocks = new ConcurrentHashMap<>();

    @Value("${io.lastwill.eventscan.network-speed.interval}")
    private int interval;

    private Instant startTime;

    @PostConstruct
    protected void init() {
        Stream.of(NetworkType.values())
                .forEach(networkType -> blocks.put(networkType, Collections.synchronizedList(new ArrayList<>())));
        startTime = Instant.now();
    }

    @EventListener
    protected void onNewBlock(NewBlockEvent event) {
        NetworkType networkType = event.getNetworkType();
        Instant now = Instant.now();
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

        long passedTime = Timestamp.from(now).getTime() - Timestamp.from(startTime).getTime();
        if (passedTime > interval) {
            passedTime = interval;
        }

        return (double) count * millisInUnit / passedTime;
    }

    @Getter
    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Block {
        private final Instant receivedTime;
    }
}
