package io.lastwill.eventscan.repositories;

import io.lastwill.eventscan.model.BotUser;
import org.springframework.data.repository.CrudRepository;

public interface BotUserRepository extends CrudRepository<BotUser, Long> {
    boolean existsByChatId(long chatId);
}
