package io.lastwill.eventscan.model;

import lombok.Getter;
import lombok.ToString;

import javax.persistence.*;

@Getter
@Entity
@Table(name = "profile_profile")
@ToString(of = {"id", "user"})
public class UserProfile {
    @Id
    private Integer id;
    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;
    private String internalAddress;
    private String memo;
}
