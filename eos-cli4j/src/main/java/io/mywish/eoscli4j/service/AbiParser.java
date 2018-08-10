package io.mywish.eoscli4j.service;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class AbiParser {
    private static final String base32 = ".12345abcdefghijklmnopqrstuvwxyz";
    public String parseName(ByteBuffer buffer) {
        char[] result = new char[12];
        long i64 = buffer.order(ByteOrder.LITTLE_ENDIAN)
                .getLong();
        i64 = i64 >>> 4;
        for (int i = 0; i < 12; i ++) {
            int index = (int) (i64 & 0x1fL);
            result[11 - i] = base32.charAt(index);
            i64 = i64 >>> 5;
        }
        return new String(result);
    }
}
