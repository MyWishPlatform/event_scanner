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
import java.math.BigInteger;
import java.util.*;

@Slf4j
@ConditionalOnBean(TelegramBotsApi.class)
public class MyWishBot extends TelegramLongPollingBot {
    @Autowired
    private TelegramBotsApi telegramBotsApi;

    @Autowired
    private ChatPersister chatPersister;

    @Autowired(required = false)
    private InformationProvider informationProvider;

    private final List<BigInteger> investments = new ArrayList<>();

    @Autowired
    private List<BotCommand> commands;

    @Getter
    @Value("${io.mywish.bot.token}")
    private String botToken;
    @Getter
    @Value("${io.mywish.bot.name}")
    private String botUsername;

    public MyWishBot(DefaultBotOptions botOptions) {
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

    public void onContract(String network, Integer productId, String productType, Integer id, String cost, final String address, String addressLink) {
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
                .append(", see on [")
                .append(address)
                .append("](")
                .append(addressLink)
                .append(").")
                .toString();

        sendToAllChats(new SendMessage().enableMarkdown(true).setText(message));
    }


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

        sendToAllChats(new SendMessage().enableMarkdown(true).setText(message));
    }

    public void onBalance(String network, String account, String cost, final String txLink) {
        final String message = new StringBuilder()
                .append(network)
                .append(": payment received from user ")
                .append(account)
                .append(", [")
                .append(cost)
                .append("](https://")
                .append(txLink)
                .append(").")
                .toString();

        sendToAllChats(new SendMessage().setText(message).enableMarkdown(true));
    }

    public void onFGWBalanceChanged(String network, String delta, String balance, String link, long blockNo, String blockLink) {
        final String message = new StringBuilder()
                .append(network)
                .append(": federation GW balance changed on ")
                .append(delta)
                .append(", now it is [")
                .append(balance)
                .append("](")
                .append(link)
                .append(") at block [")
                .append(blockNo)
                .append("](")
                .append(blockLink)
                .append(").")
                .toString();

        sendToAllChats(new SendMessage().setText(message).enableMarkdown(true));
    }

    public void onBtcPayment(String network, String productType, long productId, String value, final String link) {
        final String message = new StringBuilder()
                .append(network)
                .append(": funds arrived for contract ")
                .append(productType)
                .append(" (")
                .append(productId)
                .append("): [")
                .append(value)
                .append("](")
                .append(link)
                .append(").")
                .toString();

        sendToAllChats(new SendMessage().setText(message).enableMarkdown(true));
    }

    public void onNeoPayment(final String network, final String address, final String value, final String link) {
        final String message = new StringBuilder()
                .append(network)
                .append(": funds arrived on address ")
                .append(address)
                .append(": [")
                .append(value)
                .append("](")
                .append(link)
                .append(").")
                .toString();

        sendToAllChats(new SendMessage().setText(message).enableMarkdown(true));
    }

    public void sendToAll(String message, boolean markdown) {
        sendToAllChats(new SendMessage().setText(message).enableMarkdown(markdown));
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

    private void directMessage(long chatId, String userName) {
        if (informationProvider == null || !informationProvider.isAvailable(userName)) {
            try {
                execute(new SendMessage()
                        .setChatId(chatId)
                        .setText("Not information available.")
                );
            }
            catch (TelegramApiException e) {
                log.error("Sending stub message to chat '{}' was failed.", chatId, e);
            }
            return;
        }
        SendMessage message = informationProvider.getInformation(userName);

        try {
            execute(message.setChatId(chatId));
        }
        catch (TelegramApiException e) {
            log.error("Sending message '{}' to chat '{}' was failed.", message, chatId, e);
        }
    }
}
