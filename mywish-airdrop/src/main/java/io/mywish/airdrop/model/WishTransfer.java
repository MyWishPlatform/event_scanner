package io.mywish.airdrop.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigInteger;

@Getter
@Entity
@Table(name = "airdrop_wish_transfer")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class WishTransfer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(nullable = false)
    private String fromAddress;
    @Column(nullable = false)
    private String toAddress;
    @Column(nullable = false)
    private BigInteger wishAmount;
    @Column(nullable = false)
    private String txHash;
    @Column(nullable = false)
    private long blockNumber;
}
