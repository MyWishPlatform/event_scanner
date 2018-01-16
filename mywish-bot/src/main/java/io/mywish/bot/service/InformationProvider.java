package io.mywish.bot.service;

import org.telegram.telegrambots.api.methods.send.SendMessage;

public interface InformationProvider {
    SendMessage getInformation(String userName);

    boolean isAvailable(String userName);
}
