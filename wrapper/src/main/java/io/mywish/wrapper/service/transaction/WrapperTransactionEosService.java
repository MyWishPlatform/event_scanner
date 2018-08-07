package io.mywish.wrapper.service.transaction;

import io.mywish.eoscli4j.model.ActionAuthorization;
import io.mywish.eoscli4j.model.EosAction;
import io.mywish.eoscli4j.model.Transaction;
import io.mywish.wrapper.WrapperOutput;
import io.mywish.wrapper.WrapperTransaction;
import io.mywish.wrapper.service.WrapperTransactionService;
import io.mywish.wrapper.transaction.WrapperTransactionEos;
import org.springframework.stereotype.Component;

import javax.xml.bind.DatatypeConverter;
import java.math.BigInteger;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class WrapperTransactionEosService implements WrapperTransactionService<Transaction> {
    private final static String ACCOUNT_NAME_SEPARATOR = "::";

    @Override
    public WrapperTransaction build(Transaction transaction) {
        String hash = transaction.getId();
        List<String> inputs = transaction
                .getActions()
                .stream()
                .map(EosAction::getAuthorizations)
                .flatMap(List::stream)
                .map(ActionAuthorization::getActor)
                .collect(Collectors.toList());

        List<WrapperOutput> outputs = transaction
                .getActions()
                .stream()
                .map(eosAction -> new WrapperOutput(
                        transaction.getId(),
                        0,
                        eosAction.getAccount() + ACCOUNT_NAME_SEPARATOR + eosAction.getName(),
                        BigInteger.ZERO, // TODO: parse value from data
                        DatatypeConverter.parseHexBinary(eosAction.getData())
                ))
                .collect(Collectors.toList());

        Boolean contractCreation = false; // TODO: implement
        return new WrapperTransactionEos(hash, inputs, outputs, contractCreation, transaction);
    }
}
