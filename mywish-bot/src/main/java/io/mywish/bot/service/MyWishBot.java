package io.mywish.bot.service;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import org.telegram.telegrambots.exceptions.TelegramApiRequestException;

import javax.annotation.PostConstruct;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Component
public class MyWishBot extends TelegramLongPollingBot {
    @Autowired
    private TelegramBotsApi telegramBotsApi;

    private final ConcurrentHashMap<Long, AtomicInteger> chats = new ConcurrentHashMap<>();
    private final List<BigInteger> investments = new ArrayList<>();

    @Getter
    @Value("${io.mywish.bot.token}")
    private String botToken;
    @Getter
    @Value("${io.mywish.bot.name}")
    private String botUsername;

    @PostConstruct
    protected void init() {
        try {
            telegramBotsApi.registerBot(this);
        }
        catch (TelegramApiRequestException e) {
            log.error("Failed during the bot registration.", e);
        }
    }

    @Override
    public void onUpdateReceived(Update update) {
        Long chatId;
        if (update.hasChannelPost()) {
            chatId = update.getChannelPost().getChatId();
        }
        else if (update.hasEditedChannelPost()) {
            chatId = update.getEditedChannelPost().getChatId();
        }
        else if (update.hasEditedMessage()) {
            chatId = update.getEditedMessage().getChatId();
        }
        else if (update.hasMessage()) {
            chatId = update.getMessage().getChatId();
        }
        else {
            return;
        }
        chats.putIfAbsent(
                chatId,
                new AtomicInteger()
        );
    }

    public void onInvestment(final String sender, final BigInteger weiAmount) {
        int last;
        synchronized (investments) {
            investments.add(weiAmount);
            last = investments.size();
        }

        sendMessage(last, weiAmount);
    }

    private void sendMessage(int index, BigInteger weiAmount) {
        BigInteger hundreds = weiAmount.divide(BigInteger.valueOf(10000000000000000L));
        BigInteger[] parts = hundreds.divideAndRemainder(BigInteger.valueOf(100));
        BigInteger eth = parts[0];
        int rem = parts[1].intValue();
        final String message = "New investment: " + eth + (rem > 0 ? "." + rem : "") + " ETH";
        for (long chatId: chats.keySet()) {
            try {
                execute(new SendMessage()
                        .setChatId(chatId)
                        .setText(message)
                );
            }
            catch (TelegramApiException e) {
                log.error("Sending message '{}' to chat '{}' was failed.", message, chatId, e);
                chats.remove(chatId);
            }
        }
    }
}
