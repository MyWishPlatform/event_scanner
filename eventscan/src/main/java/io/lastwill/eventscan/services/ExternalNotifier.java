package io.lastwill.eventscan.services;

import io.lastwill.eventscan.messages.BaseNotify;
import io.lastwill.eventscan.model.NetworkType;

public interface ExternalNotifier {
    void send(final NetworkType networkType, final BaseNotify notify);
}
