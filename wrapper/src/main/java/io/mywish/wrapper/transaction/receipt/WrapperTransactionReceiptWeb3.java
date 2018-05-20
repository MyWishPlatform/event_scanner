package io.mywish.wrapper.transaction.receipt;

import io.mywish.wrapper.ContractEventDefinition;
import io.mywish.wrapper.WrapperTransactionReceipt;
import io.mywish.wrapper.log.WrapperLogWeb3;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.utils.Numeric;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Map;
import java.util.stream.Collectors;

public class WrapperTransactionReceiptWeb3 extends WrapperTransactionReceipt {
    public WrapperTransactionReceiptWeb3(TransactionReceipt web3TxReceipt, Map<String, ContractEventDefinition> eventDefinitionsBySignature) {
        super(
                web3TxReceipt.getTransactionHash(),
                new ArrayList<String>() {{
                    add(web3TxReceipt.getContractAddress());
                }},
                web3TxReceipt.getLogs().stream().map(log -> new WrapperLogWeb3(log, eventDefinitionsBySignature.get(log.getTopics().get(0)))).collect(Collectors.toList()),
                (web3TxReceipt.getStatus().startsWith("0x") ? Numeric.decodeQuantity(web3TxReceipt.getStatus()) : new BigInteger(web3TxReceipt.getStatus()))
                        .compareTo(BigInteger.ZERO) != 0
        );
    }
}
