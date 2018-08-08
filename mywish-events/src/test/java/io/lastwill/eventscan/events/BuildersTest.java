package io.lastwill.eventscan.events;

import io.lastwill.eventscan.events.builders.CreateAccountEventBuilder;
import io.lastwill.eventscan.events.builders.CreateTokenEventBuilder;
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
    }
}
