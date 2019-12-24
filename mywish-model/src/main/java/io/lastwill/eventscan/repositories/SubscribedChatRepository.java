package io.lastwill.eventscan.repositories;

import io.lastwill.eventscan.model.SubscribedChat;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

public interface SubscribedChatRepository extends CrudRepository<SubscribedChat, Long> {
    @Override
    Set<SubscribedChat> findAll();

    Set <SubscribedChat> findAllByBotName(String botName);

    boolean existsByChatIdAndBotName(Long chatId, String botName);

    @Transactional
    @Modifying
    void deleteByChatId(Long chatId);
}
