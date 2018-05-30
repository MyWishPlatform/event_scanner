package io.mywish.bot.integration.commands;

import io.mywish.bot.service.BotCommand;
import io.mywish.bot.service.ChatContext;
import lombok.Getter;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class BotCommandHello implements BotCommand {
    @Getter
    private final String name = "/hello";
    @Getter
    private final String usage = "";
    @Getter
    private final String description = "Print 'Hello'";

    @Override
    public void execute(ChatContext context, List<String> args) {
        context.sendMessage("Hello");
    }
}
