package io.mywish.bot.commands;

import org.telegram.telegrambots.bots.AbsSender;

import java.util.List;

public interface BotCommand {
    void execute(List<String> args, Long chatId, String userName, AbsSender sender);
    String getName();
}
