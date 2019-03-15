package io.mywish.bot.integration.commands;

import io.lastwill.eventscan.repositories.BotUserRepository;
import io.lastwill.eventscan.repositories.UserSiteBalanceRepository;
import io.mywish.bot.service.BotCommand;
import io.mywish.bot.service.ChatContext;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BotCommandUserInfo implements BotCommand {
    @Getter
    private final String name = "/user";
    @Getter
    private final String usage = "";
    @Getter
    private final String description = "Print information about user";

    @Autowired
    private BotUserRepository botUserRepository;

    @Autowired
    private UserSiteBalanceRepository userSiteBalanceRepository;

    @Override
    public void execute(ChatContext context, List<String> args) {
        if (!botUserRepository.existsByChatId(context.getChatId())) {
            context.sendMessage("You don't have access to this function.");
            return;
        }

        if (args.size() != 1) {
            context.sendMessage("Please specify user id or e-mail");
            return;
        }

        String idOrEmail = args.get(0);
        // todo: id, email
    }
}
