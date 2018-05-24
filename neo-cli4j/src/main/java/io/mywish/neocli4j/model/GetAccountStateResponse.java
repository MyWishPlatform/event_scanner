package io.mywish.neocli4j.model;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class GetAccountStateResponse {
    private List<Balance> balances = new ArrayList<>();
}
