package io.mywish.joule.model;

import io.lastwill.eventscan.model.Contract;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "joule_registration")
@ToString
@NoArgsConstructor
public class JouleRegistration {
    @Id
    @GeneratedValue
    private int id;
    @ManyToOne(optional = false)
    private Contract contract;
    @Column(nullable = false)
    private LocalDateTime invocationAt;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private JouleRegistrationState state;
    private LocalDateTime publishedAt;
    private String txHash;

    public JouleRegistration(Contract contract, LocalDateTime invocationAt) {
        this.contract = contract;
        this.invocationAt = invocationAt;
        this.state = JouleRegistrationState.CREATED;
    }
}
