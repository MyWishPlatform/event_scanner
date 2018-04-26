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
public class BotCommandNetworkStatus implements BotCommand {
    @Autowired
    NetworkStuckMonitor networkStuckMonitor;

    @Override
    public void execute(ChatContext context, List<String> args) {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        ZoneId zone = ZoneId.of("Europe/Moscow");
        List<String> messages = new ArrayList<>();

        for (NetworkType network : NetworkType.values()) {
            String message = network.name();
            NetworkStuckMonitor.LastEvent lastEvent = networkStuckMonitor.getLastEvents().get(network);
            if (lastEvent != null) {
                message = message +
                        "\n\tLast block: " + lastEvent.getBlockNo() +
                        "\n\tReceived time: " + ZonedDateTime.ofInstant(lastEvent.getReceivedTime().toInstant(ZoneOffset.UTC), zone).format(dateFormatter) +
                        "\n\tBlock timestamp: " + lastEvent.getTimestamp().getEpochSecond();
            }
            messages.add(message);
        }
        context.sendMessage(String.join("\n\n", messages));
    }

    @Override
    public String getName() {
        return "/networks";
    }
}
