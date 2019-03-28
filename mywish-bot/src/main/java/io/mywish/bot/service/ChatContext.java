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
            for (int start = 0, lastNewlineOffset; start < text.length(); start = start + lastNewlineOffset + 1) {
                lastNewlineOffset = text
                        .substring(start, Math.min(start + MAX_MESSAGE_LENGTH + 1, text.length()))
                        .lastIndexOf('\n');
                String shortMessage = text
                        .substring(start, Math.min(start + lastNewlineOffset + 1, text.length()))
                        .trim();
                if (!shortMessage.isEmpty()) {
                    sender.execute(message.setText(shortMessage).setChatId(chatId));
                }
                start = start + lastNewlineOffset + 1;
            }
        } catch (TelegramApiException e) {
            log.error("Error on sending message in chat {}", chatId, e);
        }
    }

    public void sendMessage(String message) {
        sendMessage(new SendMessage().setText(message));
    }
}
