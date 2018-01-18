package io.lastwill.eventscan.model;

import lombok.Getter;

import javax.persistence.*;
import java.time.ZonedDateTime;

@Getter
@MappedSuperclass
public abstract class ProductCheckable extends Product {
    @Column(name = "check_interval")
    private Integer checkInterval;
    @Column(name = "active_to")
    private ZonedDateTime activeTo;
    @Column(name = "last_check")
    private ZonedDateTime lastCheck;
    @Column(name = "next_check")
    private ZonedDateTime nextCheck;
}
