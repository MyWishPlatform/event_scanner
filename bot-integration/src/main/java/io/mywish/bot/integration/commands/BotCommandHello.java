package io.mywish.bot.integration.commands;

import io.mywish.bot.service.BotCommand;
import io.mywish.bot.service.ChatContext;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class BotCommandHello implements BotCommand {
    @Override
    public void execute(ChatContext context, List<String> args) {
        context.sendMessage("Hello");
    }

    @Override
    public String getName() {
        return "/hello";
    }
}
