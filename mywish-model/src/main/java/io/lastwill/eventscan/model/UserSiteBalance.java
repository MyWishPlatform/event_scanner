package io.lastwill.eventscan.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.math.BigInteger;
import java.time.ZonedDateTime;

@Entity
@Table(name = "profile_usersitebalance")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserSiteBalance {
    @Id
    private int id;
    private BigInteger balance;
    private String ethAddress;
    private String tronAddress;
    private String btcAddress;
    private String memo;
    @ManyToOne
    @JoinColumn(name = "subsite_id", referencedColumnName = "id")
    private Site site;
    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;
}
