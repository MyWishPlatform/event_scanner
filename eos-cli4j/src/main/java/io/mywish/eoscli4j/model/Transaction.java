package io.mywish.eoscli4j.model;

import lombok.Getter;

@Getter
public class Transaction {
    private String status;
    private TransactionDetails trx;
}
