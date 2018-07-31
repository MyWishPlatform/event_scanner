package io.mywish.eoscli4j.model;

import lombok.Getter;

import java.util.List;

@Getter
public class TransactionReceipt {
    private List<TransactionAction> actions;
}
