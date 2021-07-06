package com.codedead.deadhash.domain.utils;

import java.security.MessageDigest;
import java.util.zip.CRC32;

public final class HashUtil {

    /**
     * Initialize a new HashUtil
     */
    private HashUtil() {
        // Default constructor
    }

    /**
     * Convert a byte array to a hexadecimal String object
     *
     * @param data The byte array that should be converted
     * @return The hexadecimal String object that represents the byte array
     */
    private static String convertToHex(final byte[] data) {
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

    /**
     * Calculate the hash of an array of bytes using the specified message digest
     *
     * @param bytes The byte array that should be used to calculate a hash
     * @param kind  The message digest
     * @return The String object that contains the hash of the byte array using the specified message digest
     */
    public static String calculateHash(final byte[] bytes, final String kind) {
        try {
            final MessageDigest md = MessageDigest.getInstance(kind);
            md.update(bytes, 0, bytes.length);
            final byte[] hash = md.digest();
            return convertToHex(hash);
        } catch (final Exception ex) {
            return null;
        }
    }

    /**
     * Calculate the CRC32 value of a specified byte array
     *
     * @param bytes The byte array that should be used to calculate the CRC32 value
     * @return The String object that represents the CRC32 value of the given byte array
     */
    public static String calculateCRC32(final byte[] bytes) {
        try {
            final CRC32 crc = new CRC32();
            crc.update(bytes);
            return Long.toHexString(crc.getValue());
        } catch (final Exception ex) {
            return null;
        }
    }
}
