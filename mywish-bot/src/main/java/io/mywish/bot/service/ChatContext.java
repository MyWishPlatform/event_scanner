package io.mywish.bot.service;

import lombok.Getter;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.bots.AbsSender;
import org.telegram.telegrambots.exceptions.TelegramApiException;

public class ChatContext {
    private AbsSender sender;

    @Getter
    private Long chatId;

    @Getter
    private String userName;

    public ChatContext(AbsSender sender) {
        this.sender = sender;
    }

    public void sendMessage(SendMessage message) {
        try {
            sender.execute(message.setChatId(chatId));
        } catch(TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(String message) {
        sendMessage(new SendMessage().setText(message));
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
