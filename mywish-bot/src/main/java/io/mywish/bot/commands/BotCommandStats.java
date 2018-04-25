package io.mywish.bot.commands;

import io.mywish.bot.service.InformationProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.AbsSender;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import java.util.List;

@Component
public class BotCommandStats implements BotCommand {
    @Autowired
    private InformationProvider informationProvider;

    @Override
    public void execute(List<String> args, Long chatId, String userName, AbsSender sender) {
        try {
            sender.execute(informationProvider.getInformation(userName).setChatId(chatId));
        } catch(TelegramApiException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getName() {
        return "stats";
    }
}
