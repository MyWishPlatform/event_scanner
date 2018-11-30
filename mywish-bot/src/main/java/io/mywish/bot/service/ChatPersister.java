package io.mywish.bot.service;

public interface ChatPersister {
    boolean tryAdd(long chatId);

    Iterable<Long> getChats();

    int getCount();

    void remove(long chatId);
}
