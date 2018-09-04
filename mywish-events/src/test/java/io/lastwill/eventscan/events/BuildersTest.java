package io.lastwill.eventscan.events;

import io.lastwill.eventscan.events.builders.CreateAccountEventBuilder;
import io.lastwill.eventscan.events.builders.CreateTokenEventBuilder;
import io.lastwill.eventscan.events.builders.InitializedEventBuilder;
import io.lastwill.eventscan.events.builders.SetCodeEventBuilder;
import io.lastwill.eventscan.events.builders.crowdsale.FinalizedEventBuilder;
import io.lastwill.eventscan.events.builders.crowdsale.SetFinishEventBuilder;
import io.lastwill.eventscan.events.builders.crowdsale.SetStartEventBuilder;
import io.lastwill.eventscan.events.builders.eos.EosTransferEventBuilder;
import io.mywish.wrapper.ContractEventBuilder;
import org.junit.Assert;
import org.junit.Test;

public class BuildersTest {
    @Test
    public void outputSignature() {
        checkBuilder(new CreateAccountEventBuilder(), "0xc439029223ffeb43b811c70e26388a081f73f241e7766b81b925e1a1606e6fe8");
        checkBuilder(new CreateTokenEventBuilder(), "0xfad54e54b8f76b6e89b6b455eca7b7f86a1780dd4552d2b586669a81831efeb4");
        checkBuilder(new EosTransferEventBuilder(), "0x734eb4304d685e75d086f32b6d152f26dcb5b67966530fcfcb88e2236026fbdb");
        checkBuilder(new SetCodeEventBuilder(), "0x49f847db0524ed54d691f44de815e9d561ce771fcfccb8629ed5c3e6c4df664e");
        checkBuilder(new InitializedEventBuilder(), "0x5daa87a0e9463431830481fd4b6e3403442dfb9a12b9c07597e9f61d50b633c8");
        checkBuilder(new SetFinishEventBuilder(), "0x5702595fbe7aaf5e94baa93c3f0179c3c82dbfbc2e6386ac3cca8e87457fce35");
        checkBuilder(new SetStartEventBuilder(), "0xb2860396c193df8f7dcc188ad8c74f6cbd69e24c025830008b224906f94d9455");
        checkBuilder(new FinalizedEventBuilder(), "0x6823b073d48d6e3a7d385eeb601452d680e74bb46afe3255a7d778f3a9b17681");
    }

    private void checkBuilder(ContractEventBuilder builder, String expectedSignature) {
        System.out.println(builder.getDefinition().getSignature() + " -> " + builder.getDefinition().getName());
        if (expectedSignature == null) {
            return;
        }
        Assert.assertEquals("See " + builder.getClass().getSimpleName(), expectedSignature, builder.getDefinition().getSignature());
    }
}
