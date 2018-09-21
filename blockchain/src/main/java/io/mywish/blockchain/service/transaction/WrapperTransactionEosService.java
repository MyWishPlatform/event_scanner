package io.mywish.blockchain.service.transaction;

import io.mywish.eoscli4j.model.ActionAuthorization;
import io.mywish.eoscli4j.model.EosAction;
import io.mywish.eoscli4j.model.Transaction;
import io.mywish.blockchain.WrapperOutput;
import io.mywish.blockchain.WrapperTransaction;
import io.mywish.blockchain.model.output.WrapperOutputEos;
import io.mywish.blockchain.service.WrapperTransactionService;
import io.mywish.blockchain.transaction.WrapperTransactionEos;
import org.springframework.stereotype.Component;

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
                .map(eosAction -> new WrapperOutputEos(
                        transaction.getId(),
                        eosAction.getAccount(),
                        eosAction.getName(),
                        eosAction.getData()
                ))
                .collect(Collectors.toList());

        Boolean contractCreation = false; // TODO: implement
        return new WrapperTransactionEos(
                hash,
                inputs,
                outputs,
                contractCreation,
                transaction.getStatus()
        );
    }
}
