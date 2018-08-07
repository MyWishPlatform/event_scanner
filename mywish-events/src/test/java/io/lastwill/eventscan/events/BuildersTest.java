package io.lastwill.eventscan.events;

import io.lastwill.eventscan.events.builders.CreateAccountEventBuilder;
import org.junit.Assert;
import org.junit.Test;

public class BuildersTest {
    @Test
    public void outputSignature() {
        CreateAccountEventBuilder builder = new CreateAccountEventBuilder();
        Assert.assertEquals("See WrapperLogEosService.","0xc439029223ffeb43b811c70e26388a081f73f241e7766b81b925e1a1606e6fe8", builder.getDefinition().getSignature());
        System.out.println(builder.getDefinition().getSignature() + " -> " + builder.getDefinition().getName());
//        System.out.println(builder.getDefinition().getSignature() + " -> " + builder.getDefinition().getName());
    }
}
