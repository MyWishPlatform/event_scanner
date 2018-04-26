package io.mywish.bot.integration.commands;

import io.mywish.bot.service.BotCommand;
import io.mywish.bot.service.ChatContext;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class BotCommandEcho implements BotCommand {
    @Override
    public void execute(ChatContext context, List<String> args) {
        context.sendMessage(String.join(" ", args));
    }

    @Override
    public String getName() {
        return "/echo";
    }

    @Override
    public String getUsage() {
        return "arg1, arg2, ...";
    }

    @Override
    public String getDescription() {
        return "Print a line";
    }
}
