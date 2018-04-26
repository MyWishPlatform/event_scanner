package io.mywish.bot.service;

import io.mywish.bot.service.ChatContext;
import lombok.Getter;

import java.util.List;

public interface BotCommand {
    void execute(ChatContext context, List<String> args);
    String getName();
    String getUsage();
    String getDescription();
}
