package io.mywish.bot.commands;

import io.mywish.bot.service.ChatContext;
import io.mywish.bot.service.InformationProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class BotCommandStats implements BotCommand {
    @Autowired
    private InformationProvider informationProvider;

    @Override
    public void execute(ChatContext context, List<String> args) {
        context.sendMessage(informationProvider.getInformation(context.getUserName()));
    }

    @Override
    public String getName() {
        return "/stats";
    }
}
