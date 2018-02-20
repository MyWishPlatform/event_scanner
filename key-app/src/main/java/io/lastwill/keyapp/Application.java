package io.lastwill.keyapp;

import lombok.extern.slf4j.Slf4j;
import org.ethereum.crypto.ECKey;
import org.spongycastle.util.encoders.Hex;

import java.security.SecureRandom;

@Slf4j
public class Application {
    private static SecureRandom random = new SecureRandom();

    public static void main(String[] args) {
        ECKey key = new ECKey(random);
        log.info("Key {}.", key.toStringWithPrivate());
        log.info("Key address {}.", Hex.toHexString(key.getAddress()));
    }
}
