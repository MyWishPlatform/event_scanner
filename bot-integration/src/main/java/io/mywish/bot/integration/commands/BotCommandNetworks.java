package io.mywish.bot.integration.commands;

import io.lastwill.eventscan.services.monitors.NetworkStuckMonitor;
import io.mywish.bot.service.BotCommand;
import io.mywish.bot.service.ChatContext;
import io.mywish.scanner.model.NetworkType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Component
public class BotCommandNetworks implements BotCommand {
    @Autowired
    private NetworkStuckMonitor networkStuckMonitor;

    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private final ZoneId zone = ZoneId.of("Europe/Moscow");

    @Override
    public void execute(ChatContext context, List<String> args) {
        List<String> messages = new ArrayList<>();
        Map<NetworkType, NetworkStuckMonitor.LastEvent> lastEvents = networkStuckMonitor.getLastEvents();
        for (NetworkType network : NetworkType.values()) {
            NetworkStuckMonitor.LastEvent lastEvent = lastEvents.get(network);
            if (lastEvent != null) {
                messages.add(network.name() +
                        "\n\tLast block: " + lastEvent.getBlockNo() +
                        "\n\tReceived time: " + ZonedDateTime.ofInstant(lastEvent.getReceivedTime().toInstant(ZoneOffset.UTC), zone).format(dateFormatter) +
                        "\n\tBlock timestamp: " + lastEvent.getTimestamp().getEpochSecond()
                );
            }
        }
        context.sendMessage(String.join("\n\n", messages));
    }

    @Override
    public String getName() {
        return "/networks";
    }

    @Override
    public String getUsage() {
        return "";
    }

    @Override
    public String getDescription() {
        return "Command list with their short descriptions";
    }
}
