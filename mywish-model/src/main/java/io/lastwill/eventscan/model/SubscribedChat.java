package io.lastwill.eventscan.model;

import lombok.Getter;

import javax.persistence.*;

@Entity
@Table(name = "chat")
@Getter
public class SubscribedChat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "chat_id", unique = true, nullable = false)
    private Long chatId;

    public SubscribedChat() {
    }

    public SubscribedChat(Long chatId) {
        this.chatId = chatId;
    }
}
