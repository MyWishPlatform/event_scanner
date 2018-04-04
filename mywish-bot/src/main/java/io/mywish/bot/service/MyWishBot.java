package io.mywish.bot.service;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

@Slf4j
@Component
public class MyWishBot extends TelegramLongPollingBot {
    @Autowired
    private TelegramBotsApi telegramBotsApi;

    @Autowired
    private ChatPersister chatPersister;

    @Autowired(required = false)
    private InformationProvider informationProvider;

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
        else if (update.hasMessage()) {
            chatId = update.getMessage().getChatId();
            String userName = update.getMessage().getFrom() != null
                    ? update.getMessage().getFrom().getUserName()
                    : null;
            if (update.getMessage().getChat().isUserChat()) {
                log.debug("Direct message received from {}.", update.getMessage().getFrom());
                directMessage(chatId, userName);
            }
            else {
                log.debug("Bot mentioned in chat {}.", chatId);
                directMessage(chatId, userName);
            }

        }
        else {
            return;
        }
        if (chatPersister.tryAdd(chatId)) {
            log.info("Bot was added to the chat {}. Now he is in {} chats.", chatId, chatPersister.getCount());
        }
    }

    public void onInvestment(final String sender, final BigInteger weiAmount) {
        log.info("Investment received from {}, amount {}.", sender, weiAmount);
        int last;
        synchronized (investments) {
            investments.add(weiAmount);
            last = investments.size();
        }

        sendMessage(last, weiAmount);
    }

    public void onContract(String network, Integer productId, String productType, Integer id, BigInteger cost, final String address, String etherscanHost) {
        final String message = new StringBuilder()
                .append(network)
                .append(": new contract ")
                .append(productType)
                .append(" (")
                .append(productId)
                .append(", ")
                .append(id)
                .append(") was created for ")
                .append(toEth(cost))
                .append(" ETH, see on [etherscan](https://")
                .append(etherscanHost)
                .append("/address/")
                .append(address)
                .append(").")
                .toString();

        sendToAllChats(new SendMessage().enableMarkdown(true).setText(message));
    }


    public void onContractFailed(String network, Integer productId, String productType, Integer id, final String txHash, String etherscanHost) {
        final String message = new StringBuilder()
                .append(network)
                .append(": *failed* contract creation ")
                .append(productType)
                .append(" (")
                .append(productId)
                .append(", ")
                .append(id)
                .append(")! See on [etherscan](https://")
                .append(etherscanHost)
                .append("/tx/")
                .append(txHash)
                .append(").")
                .toString();

        sendToAllChats(new SendMessage().enableMarkdown(true).setText(message));
    }

    public void onBalance(String network, Integer id, BigInteger cost, final String currency, String txHash, String etherscanHost) {
        final String message = new StringBuilder()
                .append(network)
                .append(": payment received from user ")
                .append(id)
                .append(": [")
                .append(toEth(cost))
                .append(" ")
                .append(currency)
                .append("](https://")
                .append(etherscanHost)
                .append("/tx/")
                .append(txHash)
                .append(").")
                .toString();

        sendToAllChats(new SendMessage().setText(message).enableMarkdown(true));
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

    private void sendMessage(int index, BigInteger weiAmount) {
        String eth = toEth(weiAmount);
        final String message = "New investment: " + eth + " ETH";
        sendToAllChats(new SendMessage().setText(message));
    }

    private static String toEth(BigInteger weiAmount) {
        BigInteger hundreds = weiAmount.divide(BigInteger.valueOf(10000000000000000L));
        BigInteger[] parts = hundreds.divideAndRemainder(BigInteger.valueOf(100));
        BigInteger eth = parts[0];
        int rem = parts[1].intValue();
        String sRem;
        if (rem == 0) {
            sRem = "";
        }
        else if (rem < 10) {
            sRem = ".0" + rem;
        }
        else {
            sRem = "." + rem;
        }
        return eth + sRem;
    }
}
