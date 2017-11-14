package io.mywish.dream.model;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Id;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Contract {
    @Id
    private String address;
    private String ownerAddress;
    private String transactionHash;
    @Setter
    private long blockNumber;

    public Contract(String address, String ownerAddress, String transactionHash) {
        this.address = address;
        this.ownerAddress = ownerAddress;
        this.transactionHash = transactionHash;
    }
}
