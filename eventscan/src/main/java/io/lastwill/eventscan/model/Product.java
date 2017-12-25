package io.lastwill.eventscan.model;

import lombok.Getter;

import javax.persistence.*;
import java.math.BigInteger;

@Entity
@Table(name = "contracts_contract")
@Getter
@SqlResultSetMapping(name = "productStatistics", classes =
        @ConstructorResult(
                targetClass = ProductStatistics.class,
                columns = {
                        @ColumnResult(name = "contract_type", type = Integer.class),
                        @ColumnResult(name = "contract_state", type = String.class),
                        @ColumnResult(name = "contract_count", type = Integer.class),
                }
        )
)
@NamedNativeQuery(
        name = "Product.productStatistics",
        query = "SELECT contract_type, state contract_state, count(id) contract_count\n" +
                "FROM contracts_contract\n" +
                "GROUP BY contract_type, state\n" +
                "ORDER BY contract_type",
        resultSetMapping = "productStatistics"
)
public class Product {
    @Id
    private Integer id;
    private String ownerAddress;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ContractState state;
    private BigInteger balance;
    @Column(nullable = false)
    private BigInteger cost;
    private Integer contractType;
}
