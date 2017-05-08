package com.codedead.deadline.deadhash.domain;

import java.security.MessageDigest;
import java.util.zip.CRC32;

final class HashService {

    private static String convertToHex(byte[] data) {
        StringBuilder buf = new StringBuilder();
        for (byte b : data) {
            int halfByte = (b >>> 4) & 0x0F;
            int two_Halves = 0;
            do {
                buf.append((0 <= halfByte) && (halfByte <= 9) ? (char) ('0' + halfByte) : (char) ('a' + (halfByte - 10)));
                halfByte = b & 0x0F;
            } while (two_Halves++ < 1);
        }
        return buf.toString();
    }

    static String calculateHash(byte[] bytes, String kind) {
        try {
            MessageDigest md = MessageDigest.getInstance(kind);
            md.update(bytes, 0, bytes.length);
            byte[] hash = md.digest();
            return convertToHex(hash);
        } catch (Exception ex) {
            return null;
        }
    }

    static String calculateCRC32(byte[] bytes) {
        try {
            CRC32 crc = new CRC32();
            crc.update(bytes);

            return Long.toHexString(crc.getValue());
        } catch (Exception ex) {
            return null;
        }
    }
}
