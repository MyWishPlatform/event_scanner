package io.lastwill.eventscan.events;

import io.lastwill.eventscan.events.builders.CreateAccountEventBuilder;
import io.lastwill.eventscan.events.builders.CreateTokenEventBuilder;
import io.lastwill.eventscan.events.builders.SetCodeEventBuilder;
import io.lastwill.eventscan.events.builders.TriggeredEventBuilder;
import io.lastwill.eventscan.events.builders.eos.EosTransferEventBuilder;
import io.lastwill.eventscan.events.builders.erc20.TransferERC20EventBuilder;
import io.lastwill.eventscan.events.builders.erc223.TransferERC223EventBuilder;
import org.junit.Assert;
import org.junit.Test;

public class BuildersTest {
    @Test
    public void outputSignature() {
        CreateAccountEventBuilder accountEventBuilder = new CreateAccountEventBuilder();
        Assert.assertEquals("See WrapperLogEosService.","0xc439029223ffeb43b811c70e26388a081f73f241e7766b81b925e1a1606e6fe8", accountEventBuilder.getDefinition().getSignature());
        System.out.println(accountEventBuilder.getDefinition().getSignature() + " -> " + accountEventBuilder.getDefinition().getName());

        CreateTokenEventBuilder createTokenEventBuilder = new CreateTokenEventBuilder();
        Assert.assertEquals("See WrapperLogEosService.","0xfad54e54b8f76b6e89b6b455eca7b7f86a1780dd4552d2b586669a81831efeb4", createTokenEventBuilder.getDefinition().getSignature());
        System.out.println(createTokenEventBuilder.getDefinition().getSignature() + " -> " + createTokenEventBuilder.getDefinition().getName());

        EosTransferEventBuilder eosTransferEventBuilder = new EosTransferEventBuilder();
        Assert.assertEquals("See WrapperLogEosService.","0x734eb4304d685e75d086f32b6d152f26dcb5b67966530fcfcb88e2236026fbdb", eosTransferEventBuilder.getDefinition().getSignature());
        System.out.println(eosTransferEventBuilder.getDefinition().getSignature() + " -> " + eosTransferEventBuilder.getDefinition().getName());

        SetCodeEventBuilder setCodeEventBuilder = new SetCodeEventBuilder();
        Assert.assertEquals("See WrapperLogEosService.","0x49f847db0524ed54d691f44de815e9d561ce771fcfccb8629ed5c3e6c4df664e", setCodeEventBuilder.getDefinition().getSignature());
        System.out.println(setCodeEventBuilder.getDefinition().getSignature() + " -> " + setCodeEventBuilder.getDefinition().getName());
    }
}
