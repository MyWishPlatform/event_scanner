package io.mywish.bot.integration.commands;

import io.mywish.bot.service.BotCommand;
import io.mywish.bot.service.ChatContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class BotCommandCommands implements BotCommand {
    @Autowired
    private List<BotCommand> commands;

    @Override
    public void execute(ChatContext context, List<String> args) {
        context.sendMessage(String.join("\n", commands.stream().map(cmd -> cmd.getName() + " " + cmd.getUsage()).collect(Collectors.toList())));
    }

    @Override
    public String getName() {
        return "/commands";
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