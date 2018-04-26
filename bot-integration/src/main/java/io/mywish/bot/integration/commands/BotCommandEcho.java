package io.mywish.bot.integration.commands;

import io.mywish.bot.service.BotCommand;
import io.mywish.bot.service.ChatContext;
import lombok.Getter;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class BotCommandEcho implements BotCommand {
    @Getter
    private final String name = "/echo";
    @Getter
    private final String usage = "arg1, arg2, ...";
    @Getter
    private final String description = "Print a line";

    @Override
    public void execute(ChatContext context, List<String> args) {
        context.sendMessage(String.join(" ", args));
    }
}
