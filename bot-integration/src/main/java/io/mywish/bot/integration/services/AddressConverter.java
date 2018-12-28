package io.mywish.bot.integration.services;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.bitcoinj.core.Base58;
import org.bouncycastle.crypto.ExtendedDigest;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.util.encoders.Hex;

import java.nio.ByteBuffer;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class AddressConverter {
    public static String toTronPubKeyFrom(String hex) {
        return toWif(Hex.decode(hex.substring(2)), (byte) 0x41, new SHA256Digest());
    }

    public static String toWif(byte[] keyBytes, byte prefix, ExtendedDigest digest) {
        ByteBuffer buffer = ByteBuffer.wrap(new byte[keyBytes.length + 5]);
        buffer.put(prefix);
        buffer.put(keyBytes);

        byte[] digestBytes = calcDigest(buffer.array(), 0, keyBytes.length + 1, digest,true);
        buffer.put(digestBytes, 0, 4);

        return Base58.encode(buffer.array());
    }

    private static byte[] calcDigest(byte[] exKey, int offset, int length, ExtendedDigest digest, boolean secondPass) {
        digest.update(exKey, offset, length);
        byte[] digestFirstPass = new byte[digest.getDigestSize()];
        digest.doFinal(digestFirstPass, 0);
        digest.reset();
        if (!secondPass) {
            return digestFirstPass;
        }

        byte[] digestSecondPass = new byte[digest.getDigestSize()];
        digest.update(digestFirstPass, 0, digestFirstPass.length);
        digest.doFinal(digestSecondPass, 0);
        return digestSecondPass;
    }
}
