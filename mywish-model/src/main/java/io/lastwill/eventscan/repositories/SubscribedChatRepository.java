package io.lastwill.eventscan.repositories;

import io.lastwill.eventscan.model.SubscribedChat;
import org.springframework.data.repository.CrudRepository;

import java.util.Set;

public interface SubscribedChatRepository extends CrudRepository<SubscribedChat, Long> {
    @Override
    Set<SubscribedChat> findAll();

    boolean existsByChatId(Long chatId);

    void deleteByChatId(Long chatId);
}
