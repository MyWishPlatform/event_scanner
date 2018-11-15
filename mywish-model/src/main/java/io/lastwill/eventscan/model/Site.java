package io.lastwill.eventscan.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "profile_subsite")
public class Site {
    @Id
    private int id;
    private String siteName;
    private String currencies;
}
