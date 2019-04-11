package io.mywish.waves.blockchain.builders;

import com.wavesplatform.wavesj.Transaction;
import com.wavesplatform.wavesj.transactions.SetScriptTransaction;
import io.lastwill.eventscan.events.model.contract.waves.SetScriptEvent;
import io.mywish.blockchain.ContractEvent;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Getter
@Component
public class SetScriptWavesEventBuilder extends WavesEventBuilder<SetScriptEvent> {
    @Override
    public List<ContractEvent> build(Transaction transaction) {
        List<ContractEvent> events = new ArrayList<>();
        if (transaction instanceof SetScriptTransaction) {
            SetScriptTransaction setScript = (SetScriptTransaction) transaction;
            events.add(new SetScriptEvent(setScript.getSenderPublicKey().getAddress()));
        }

        return events;
    }
}