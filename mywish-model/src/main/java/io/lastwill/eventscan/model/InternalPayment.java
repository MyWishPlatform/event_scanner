package io.lastwill.eventscan.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigInteger;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments_internalpayment")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class InternalPayment {
    @Id
    private Integer id;
    private BigInteger delta;
    private String txHash;
    private LocalDateTime datetime;
    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;
    private String originalCurrency;
    private String originalDelta;
    @ManyToOne
    @JoinColumn(name = "site_id", referencedColumnName = "id")
    private Site site;
}
