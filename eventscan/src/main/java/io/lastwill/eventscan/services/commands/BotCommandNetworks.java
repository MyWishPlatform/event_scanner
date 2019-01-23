package io.lastwill.eventscan.services.commands;

import io.lastwill.eventscan.model.NetworkType;
import io.lastwill.eventscan.services.monitors.NetworkSpeedMonitor;
import io.lastwill.eventscan.services.monitors.NetworkStuckMonitor;
import io.mywish.bot.service.BotCommand;
import io.mywish.bot.service.ChatContext;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class BotCommandNetworks implements BotCommand {
    @Getter
    private final String name = "/networks";
    @Getter
    private final String usage = "";
    @Getter
    private final String description = "Information about latest blocks in each network";

    @Autowired
    private NetworkStuckMonitor networkStuckMonitor;

    @Autowired
    private NetworkSpeedMonitor networkSpeedMonitor;

    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final ZoneId zone = ZoneId.of("Europe/Moscow");

    @Override
    public void execute(ChatContext context, List<String> args) {
        List<String> messages = new ArrayList<>();
        Map<NetworkType, NetworkStuckMonitor.LastEvent> lastEvents = networkStuckMonitor.getLastBlockEvents();
        Map<NetworkType, NetworkStuckMonitor.LastEvent> lastPendingTxEvents = networkStuckMonitor.getLastPendingTxEvents();
        Map<NetworkType, Long> speeds = networkSpeedMonitor.getSpeeds();
        Instant speedTime = networkSpeedMonitor.getNow();
        for (NetworkType network : NetworkType.values()) {
            NetworkStuckMonitor.LastEvent lastEvent = lastEvents.get(network);
            if (lastEvent == null) {
                continue;
            }
            long blockNo = lastEvent.getBlockNo();
            String lastBlockStr = "\n\tLast block: " + blockNo;
            String receivedTimeStr = "\n\tReceived time: " + formatToLocal(lastEvent.getReceivedTime());
            String blockTimeStr = "\n\tBlock time: " +
                    ZonedDateTime.ofInstant(lastEvent.getTimestamp(), zone).format(dateFormatter);

            String lastPendingTime = null;
            if (lastPendingTxEvents.containsKey(network)) {
                NetworkStuckMonitor.LastEvent lastPendingEvent = lastPendingTxEvents.get(network);
                lastPendingTime = formatToLocal(lastPendingEvent.getReceivedTime());
            }
            String pendingTimeStr = lastPendingTime != null
                    ? "\n\tPending time: " + lastPendingTime
                    : "";

            Long speed = speeds.getOrDefault(network, 0L);
            String zonedSpeedTime = speedTime != null
                    ? ZonedDateTime.ofInstant(speedTime, zone).format(dateFormatter)
                    : null;
            String speedStr = speedTime != null
                    ? "\n\tSpeed: " + speed + " (for " + zonedSpeedTime + ")"
                    : "";

            messages.add(network.name() +
                    lastBlockStr +
                    receivedTimeStr +
                    blockTimeStr +
                    pendingTimeStr +
                    speedStr
            );
        }
        context.sendMessage(String.join("\n\n", messages));
    }

    private String formatToLocal(LocalDateTime localDateTime) {
        return ZonedDateTime.ofInstant(
                localDateTime.toInstant(ZoneOffset.UTC),
                zone
        )
                .format(dateFormatter);
    }
}
