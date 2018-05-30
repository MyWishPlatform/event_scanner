package io.mywish.bot.service;

import java.util.List;

public interface BotCommand {
    void execute(ChatContext context, List<String> args);
    String getName();
    String getUsage();
    String getDescription();
}
