package io.mywish.neocli4j.model;

import lombok.Getter;
import java.util.List;

@Getter
public class AccountState {
    private List<Balance> balances;
}
