package io.mywish.wrapper.service.output;

import io.mywish.wrapper.WrapperOutput;
import lombok.extern.slf4j.Slf4j;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.core.ScriptException;
import org.bitcoinj.core.Transaction;
import org.bitcoinj.core.TransactionOutput;
import org.bitcoinj.script.Script;
import org.springframework.stereotype.Component;

import java.math.BigInteger;

@Component
@Slf4j
public class WrapperOutputBtcService {
    public WrapperOutput build(Transaction transaction, TransactionOutput output, NetworkParameters networkParameters) {
        Script script;
        try {
            script = output.getScriptPubKey();
        }
        catch (ScriptException ex) {
            log.warn("Skip output with script error: ", output, ex);
            return null;
        }
        if (!script.isSentToAddress() && !script.isPayToScriptHash() && !script.isSentToRawPubKey()) {
            log.debug("Skip output with not appropriate script {}.", script);
            return null;
        }
        return new WrapperOutput(
                transaction.getHashAsString(),
                output.getIndex(),
                output
                        .getScriptPubKey()
                        .getToAddress(networkParameters, true)
                        .toBase58(),
                BigInteger.valueOf(output.getValue().getValue())
        );
    }
}
