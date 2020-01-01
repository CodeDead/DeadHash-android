package com.codedead.deadline.deadhash.domain;

import java.security.MessageDigest;
import java.util.zip.CRC32;

final class HashService {

    private static String convertToHex(byte[] data) {
        final StringBuilder buf = new StringBuilder();
        for (final byte b : data) {
            int halfByte = (b >>> 4) & 0x0F;
            int two_Halves = 0;
            do {
                buf.append(halfByte <= 9 ? (char) ('0' + halfByte) : (char) ('a' + halfByte - 10));
                halfByte = b & 0x0F;
            } while (two_Halves++ < 1);
        }
        return buf.toString();
    }

    static String calculateHash(byte[] bytes, String kind) {
        try {
            final MessageDigest md = MessageDigest.getInstance(kind);
            md.update(bytes, 0, bytes.length);
            final byte[] hash = md.digest();
            return convertToHex(hash);
        } catch (Exception ex) {
            return null;
        }
    }

    static String calculateCRC32(byte[] bytes) {
        try {
            final CRC32 crc = new CRC32();
            crc.update(bytes);

            return Long.toHexString(crc.getValue());
        } catch (Exception ex) {
            return null;
        }
    }
}
