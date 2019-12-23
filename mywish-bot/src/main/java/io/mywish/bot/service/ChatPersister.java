package io.mywish.bot.service;

public interface ChatPersister {
    boolean tryAdd(long chatId, String botName);

    Iterable<Long> getChats();

    Iterable<Long> getChatsByBotName(String botName);

    int getCount();

    void remove(long chatId);
}
