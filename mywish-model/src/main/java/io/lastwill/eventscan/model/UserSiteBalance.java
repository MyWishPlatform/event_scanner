package io.lastwill.eventscan.model;

import lombok.Getter;
import lombok.ToString;

import javax.persistence.*;
import java.math.BigInteger;
import java.time.ZonedDateTime;

@Entity
@Table(name = "profile_usersitebalance")
@Getter
public class UserSiteBalance {
    @Id
    private int id;
    private BigInteger balance;
    private String ethAddress;
    private String btcAddress;
    private String memo;
    @ManyToOne
    @JoinColumn(name = "site_id", referencedColumnName = "id")
    private Site site;
    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;
}
