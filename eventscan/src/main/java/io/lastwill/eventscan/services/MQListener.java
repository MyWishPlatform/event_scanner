package io.lastwill.eventscan.services;

public interface MQListener {
    void onListen(String queueMessage);
}
