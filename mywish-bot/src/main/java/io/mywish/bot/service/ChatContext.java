package io.mywish.bot.service;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.bots.AbsSender;
import org.telegram.telegrambots.exceptions.TelegramApiException;

@Slf4j
public class ChatContext {
    private final AbsSender sender;

    @Getter
    private final Long chatId;

    @Getter
    private final String userName;

    public ChatContext(AbsSender sender, Long chatId, String userName) {
        this.sender = sender;
        this.chatId = chatId;
        this.userName = userName;
    }

    public void sendMessage(SendMessage message) {
        try {
            sender.execute(message.setChatId(chatId));
        } catch(TelegramApiException e) {
            log.error("Error on sending message in chat {} with message '{}'", chatId, e.getLocalizedMessage(), e);
        }
    }

    public void sendMessage(String message) {
        sendMessage(new SendMessage().setText(message));
    }
}
