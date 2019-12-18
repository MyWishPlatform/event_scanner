package io.mywish.bot.service;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.event.EventListener;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import org.telegram.telegrambots.exceptions.TelegramApiRequestException;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Slf4j
@ConditionalOnBean(TelegramBotsApi.class)
public class MyWishBotLight extends TelegramLongPollingBot {

    @Autowired
    private TelegramBotsApi telegramBotsApi;

    @Autowired
    private ChatPersister chatPersister;

    @Autowired(required = false)
    private InformationProvider informationProvider;

    @Autowired
    private List<BotCommand> commands;

    @Getter
    @Value("${io.mywish.botLight.token}")
    private String botToken;
    @Getter
    @Value("${io.mywish.botLight.name}")
    private String botUsername;


    public MyWishBotLight(DefaultBotOptions botOptions) {
        super(botOptions);
    }

    @PostConstruct
    protected void init() {
        try {
            telegramBotsApi.registerBot(this);
            log.info("Bot was registered, token: {}.", botToken);
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
        else if (update.hasMessage() && update.getMessage().hasText()) {
            chatId = update.getMessage().getChatId();
            String userName = update.getMessage().getFrom() != null
                    ? update.getMessage().getFrom().getUserName()
                    : null;
            ChatContext chatContext = new ChatContext(this, chatId, userName);
            List<String> args = new ArrayList<>(Arrays.asList(update.getMessage().getText().split(" ")));
            String cmdName = args.remove(0);
            Optional<BotCommand> botCommand = commands
                    .stream()
                    .filter(cmd ->
                            cmd.getName().equals(cmdName)
                    )
                    .findFirst();
            if (botCommand.isPresent()) {
                botCommand.get().execute(chatContext, args);
            }
            else {
                log.warn("Unknown command {} from user {} in chat {}", cmdName, userName, chatId);
                chatContext.sendMessage("Unknown command '" + cmdName + "'");
            }
        }
        else {
            return;
        }
        if (chatPersister.tryAdd(chatId)) {
            log.info("Bot was added to the chat {}. Now he is in {} chats.", chatId, chatPersister.getCount());
        }
    }

    private void sendToAllChats(SendMessage sendMessage) {
        for (long chatId: chatPersister.getChats()) {
            try {
                // it's ok to specify chat id, because sendMessage will be serialized to JSON during the call
                execute(sendMessage.setChatId(chatId));
            }
            catch (TelegramApiException e) {
                log.error("Sending message '{}' to chat '{}' was failed.", sendMessage.getText(), chatId, e);
                chatPersister.remove(chatId);
            }
        }
    }

    public void sendToAll(String message) {
        SendMessage sendMessage = new SendMessage()
                .setText(message)
                .disableWebPagePreview();
        sendToAllChats(sendMessage);
    }

    public void sendToAllWithHtml(String message) {
        SendMessage sendMessage = new SendMessage()
                .setText(message)
                .disableWebPagePreview()
                .enableHtml(true);
        sendToAllChats(sendMessage);
    }

    public void sendToAllWithMarkdown(String message) {
        SendMessage sendMessage = new SendMessage()
                .setText(message)
                .disableWebPagePreview()
                .enableMarkdown(true);
        sendToAllChats(sendMessage);
    }

    public void onContract(String network, Integer productId, String productType, Integer id, String cost, String user, String address, String addressLink) {
        final String message = new StringBuilder()
                .append(network)
                .append(": new ")
                .append(productType)
                .append(" (")
                .append(productId)
                .append(", ")
                .append(id)
                .append(") was created for ")
                .append(cost)
                .append(" by ")
                .append(user)
                .append(", see on <a href=\"")
                .append(addressLink)
                .append("\">")
                .append(address)
                .append("</a>.")
                .toString();

        sendToAllWithHtml(message);
    }

    @EventListener
    public void onContractFailed(String network, Integer productId, String productType, Integer id, final String txLink) {
        final String message = new StringBuilder()
                .append(network)
                .append(": *failed* [contract creation ")
                .append(productType)
                .append(" (")
                .append(productId)
                .append(", ")
                .append(id)
                .append(")!](")
                .append(txLink)
                .append(").")
                .toString();

        sendToAllWithMarkdown(message);
    }

    public void onSwapsOrder(String network, Integer productId, String name, String transactionHash, String txLink) {
        final String message = new StringBuilder()
                .append(network)
                .append(": new SWAPS2 Order (")
                .append(productId)
                .append(") was created, see on [")
                .append(transactionHash)
                .append("](")
                .append(txLink)
                .append(").")
                .toString();

        sendToAllWithMarkdown(message);
    }

    public void onSwapsOrderFromDataBase(Integer productId, String name, String user) {
        final String message = new StringBuilder()
                .append("DB: new SWAPS Order (")
                .append(productId)
                .append(") (")
                .append(name)
                .append(") was created by ")
                .append(user)
                .toString();

        sendToAll(message);
    }
}