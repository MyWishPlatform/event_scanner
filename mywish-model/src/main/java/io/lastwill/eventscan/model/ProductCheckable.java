package io.lastwill.eventscan.model;

public interface ProductCheckable {
    Integer getCheckInterval();

    java.time.ZonedDateTime getActiveTo();

    java.time.ZonedDateTime getLastCheck();

    java.time.ZonedDateTime getNextCheck();
}
