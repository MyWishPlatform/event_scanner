package io.mywish.bot.service;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.bots.AbsSender;
import org.telegram.telegrambots.exceptions.TelegramApiException;

@Slf4j
public class ChatContext {
    private static final int MAX_MESSAGE_LENGTH = 4096;
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
        String text = message.getText();
        try {
            for (int start = 0, offset; start < text.length(); start = start + offset + 1) {
                String maxLengthSubstr = text.substring(start, Math.min(start + MAX_MESSAGE_LENGTH + 1, text.length()));
                int newlineIdx = maxLengthSubstr.lastIndexOf("\n");
                offset = (newlineIdx != -1) && (start + MAX_MESSAGE_LENGTH < text.length())
                        ? newlineIdx
                        : MAX_MESSAGE_LENGTH;
                String shortMessage = text
                        .substring(start, Math.min(start + offset + 1, text.length()))
                        .trim();
                if (!shortMessage.isEmpty()) {
                    sender.execute(message.setText(shortMessage).setChatId(chatId));
                }
            }
        } catch (TelegramApiException e) {
            log.error("Error on sending message in chat {}", chatId, e);
        }
    }

    public void sendMessage(String message) {
        sendMessage(new SendMessage().setText(message));
    }
}
