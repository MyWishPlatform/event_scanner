package io.mywish.bot.commands;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.bots.AbsSender;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import java.util.List;

@Component
public class BotCommandHello implements BotCommand {
    @Override
    public void execute(List<String> args, Long chatId, String userName, AbsSender sender) {
        try {
            sender.execute(new SendMessage().setChatId(chatId).setText("Hello"));
        } catch(TelegramApiException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getName() {
        return "hello";
    }
}
