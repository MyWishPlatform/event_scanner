package io.mywish.bot.integration.commands;

import io.mywish.bot.service.BotCommand;
import io.mywish.bot.service.ChatContext;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CommandHelp implements BotCommand {
    @Override
    public void execute(ChatContext context, List<String> args) {
    }

    @Override
    public String getName() {
        return "/help";
    }

    @Override
    public String getUsage() {
        return "command";
    }

    @Override
    public String getDescription() {
        return "Get description of the command";
    }
}
