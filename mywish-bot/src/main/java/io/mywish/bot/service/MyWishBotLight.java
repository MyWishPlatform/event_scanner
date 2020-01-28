package io.mywish.bot.service;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import org.telegram.telegrambots.exceptions.TelegramApiRequestException;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.text.DecimalFormat;

@Slf4j
@ConditionalOnBean(TelegramBotsApi.class)
public class MyWishBotLight extends TelegramLongPollingBot {

    @Autowired
    private TelegramBotsApi telegramBotsApiLight;

    @Autowired
    private ChatPersister chatFileLightPersister;

    @Autowired(required = false)
    private InformationProvider informationProvider;

    @Getter
    @Value("${io.mywish.bot.light.token}")
    private String botToken;
    @Getter
    @Value("${io.mywish.bot.light.name}")
    private String botUsername;
    @Value("${io.mywish.bot.swaplink}")
    private String swapLink;

    public MyWishBotLight(DefaultBotOptions botOptions) {
        super(botOptions);
    }

    @PostConstruct
    protected void init() {
        try {
            telegramBotsApiLight.registerBot(this);
            log.info("Bot was registered, token: {}.", botToken);
        } catch (TelegramApiRequestException e) {
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
        else if (update.hasMessage() && update.getMessage().hasText()) {
            chatId = update.getMessage().getChatId();
            }
        else {
            return;
        }
        if (chatFileLightPersister.tryAdd(chatId,botUsername)) {
            log.info("Bot '{}' was added to the chat {}. Now he is in {} chats.", botUsername, chatId, chatFileLightPersister.getCount());
        }
    }

    private void sendToAllChats(SendMessage sendMessage) {
        for (long chatId : chatFileLightPersister.getChatsByBotName(botUsername)) {
            try {
                // it's ok to specify chat id, because sendMessage will be serialized to JSON during the call
                execute(sendMessage.setChatId(chatId));
            } catch (TelegramApiException e) {
                log.error("Sending message '{}' to chat '{}' was failed.", sendMessage.getText(), chatId, e);
                chatFileLightPersister.remove(chatId);
            }
        }
    }

    public void sendToAll(String message) {
        SendMessage sendMessage = new SendMessage()
                .setText(message)
                .disableWebPagePreview();
        sendToAllChats(sendMessage);
    }

    public void onSwapsOrderFromDataBase(BigDecimal baseLimit, String baseName, String quoteName, BigDecimal quoteLimit, String uniqueLink) {
        DecimalFormat df = new DecimalFormat("0.##");
        final String message = new StringBuilder()
                .append("New SWAP created: ")
                .append(df.format(baseLimit))
                .append(" ")
                .append(baseName)
                .append(" <> ")
                .append(df.format(quoteLimit))
                .append(" ")
                .append(quoteName)
                .append(", see on <a href=\"")
                .append(swapLink)
                .append(uniqueLink)
                .append("\">")
                .append("SWAPSNETWORK")
                .append("</a>")
                .append(" to contribute.")
                .toString();

        sendToAllWithHtml(message);
    }

    public void sendToAllWithHtml(String message) {
        SendMessage sendMessage = new SendMessage()
                .setText(message)
                .disableWebPagePreview()
                .enableHtml(true);
        sendToAllChats(sendMessage);
    }
}