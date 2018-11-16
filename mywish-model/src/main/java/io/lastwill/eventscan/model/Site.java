package io.lastwill.eventscan.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Getter
@Entity
@Table(name = "profile_subsite")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Site {
    @Id
    private int id;
    private String siteName;
    private String currencies;
}
