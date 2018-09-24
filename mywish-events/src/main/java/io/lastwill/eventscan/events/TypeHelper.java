package io.lastwill.eventscan.events;

import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

@Component
public class TypeHelper {
    public BigInteger toBigInteger(Object o) {
        if (o == null) {
            return null;
        }
        if (o instanceof BigInteger) {
            return (BigInteger) o;
        }
        if (o instanceof String) {
            return new BigInteger((String) o);
        }
        if (o instanceof Number) {
            return BigInteger.valueOf(((Number) o).longValue());
        }
        throw new UnsupportedOperationException("Unsupported type " + o.getClass());
    }

    public byte[] toBytesArray(Object o) {
        if (o == null) {
            return null;
        }
        if (o instanceof byte[]) {
            return (byte[]) o;
        }
        if (o instanceof String) {
            return ((String)o).getBytes(StandardCharsets.US_ASCII);
        }
        throw new UnsupportedOperationException("Unsupported type " + o.getClass());
    }
}
