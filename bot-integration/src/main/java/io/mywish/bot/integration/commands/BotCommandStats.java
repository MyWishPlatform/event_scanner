package io.mywish.bot.integration.commands;

import io.mywish.bot.service.BotCommand;
import io.mywish.bot.service.ChatContext;
import io.mywish.bot.service.InformationProvider;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class BotCommandStats implements BotCommand {
    @Getter
    private final String name = "/stats";
    @Getter
    private final String usage = "";
    @Getter
    private final String description = "Information about MyWish contracts";

    @Autowired
    private InformationProvider informationProvider;

    @Override
    public void execute(ChatContext context, List<String> args) {
        context.sendMessage(informationProvider.getInformation(context.getUserName()));
    }
}
