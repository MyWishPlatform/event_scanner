package io.mywish.eos.blockchain.model;

import io.mywish.blockchain.ContractEventDefinition;
import lombok.Getter;
import org.omg.CORBA.StringHolder;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
public class EosActionFieldsDefinition extends ContractEventDefinition {
    private final Set<String> fields;

    public EosActionFieldsDefinition(String name, Set<String> fields) {
        super(name);
        this.fields = fields;
    }
}
