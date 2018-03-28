package io.lastwill.eventscan.model;

import io.mywish.scanner.model.NetworkType;
import lombok.Getter;

import javax.persistence.*;

@Entity
@Table(name = "deploy_network")
@Getter
public class Network {
    @Id
    private Integer id;
    @Column(name = "name", nullable = false)
    @Enumerated(EnumType.STRING)
    private NetworkType type;
}
