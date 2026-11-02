package com.codedead.deadhash.domain.utils;

import android.content.ContentResolver;
import android.net.Uri;
import android.os.Build;

import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;
import java.util.zip.CRC32;

import com.dynatrace.hash4j.hashing.HashStream64;
import com.dynatrace.hash4j.hashing.Hashing;

import org.bouncycastle.crypto.digests.Blake3Digest;

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
     * Calculate the hash of a specified file using the specified message digest
     *
     * @param uri             The Uri of the file that should be hashed
     * @param contentResolver The ContentResolver that should be used to open the file
     * @param kind            The message digest
     * @return The String object that contains the hash of the file using the specified message digest
     */
    public static String calculateHash(final Uri uri, final ContentResolver contentResolver, final String kind) {
        try {
            final MessageDigest md = MessageDigest.getInstance(kind);
            try (final InputStream fis = contentResolver.openInputStream(uri)) {
                if (fis == null)
                    return null;

                final byte[] dataBytes = new byte[1024];

                int nread;
                while ((nread = fis.read(dataBytes)) != -1) {
                    md.update(dataBytes, 0, nread);
                }

                final byte[] mdBytes = md.digest();
                return convertToHex(mdBytes);
            }
        } catch (final NoSuchAlgorithmException | IOException ex) {
            return null;
        }
    }

    /**
     * Calculate the BLAKE3 hash of an array of bytes
     *
     * @param bytes The byte array that should be used to calculate a hash
     * @return The String object that contains the BLAKE3 hash of the byte array
     */
    public static String calculateBlake3(final byte[] bytes) {
        try {
            final Blake3Digest digest = new Blake3Digest();
            digest.update(bytes, 0, bytes.length);

            final byte[] hash = new byte[digest.getDigestSize()];
            digest.doFinal(hash, 0);
            return convertToHex(hash);
        } catch (final Exception ex) {
            return null;
        }
    }

    /**
     * Calculate the BLAKE3 hash of a specified file
     *
     * @param uri             The Uri of the file that should be hashed
     * @param contentResolver The ContentResolver that should be used to open the file
     * @return The String object that contains the BLAKE3 hash of the file
     */
    public static String calculateBlake3(final Uri uri, final ContentResolver contentResolver) {
        try {
            final Blake3Digest digest = new Blake3Digest();
            try (final InputStream fis = contentResolver.openInputStream(uri)) {
                if (fis == null)
                    return null;

                final byte[] dataBytes = new byte[8192];

                int nread;
                while ((nread = fis.read(dataBytes)) != -1) {
                    digest.update(dataBytes, 0, nread);
                }

                final byte[] hash = new byte[digest.getDigestSize()];
                digest.doFinal(hash, 0);
                return convertToHex(hash);
            }
        } catch (final IOException ex) {
            return null;
        }
    }

    /**
     * Check whether XXH3 hashes can be calculated on this device.
     * hash4j uses MethodHandles.byteArrayViewVarHandle, which is a public API from Android 13.
     *
     * @return True if XXH3 hashes can be calculated, otherwise false
     */
    public static boolean isXxh3Supported() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU;
    }

    /**
     * Calculate the XXH3-64 hash of an array of bytes
     *
     * @param bytes The byte array that should be used to calculate a hash
     * @return The String object that contains the XXH3-64 hash of the byte array
     */
    public static String calculateXxh3(final byte[] bytes) {
        try {
            final long hash = Hashing.xxh3_64().hashBytesToLong(bytes);
            return String.format(Locale.ROOT, "%016x", hash);
        } catch (final Exception ex) {
            return null;
        }
    }

    /**
     * Calculate the XXH3-64 hash of a specified file
     *
     * @param uri             The Uri of the file that should be hashed
     * @param contentResolver The ContentResolver that should be used to open the file
     * @return The String object that contains the XXH3-64 hash of the file
     */
    public static String calculateXxh3(final Uri uri, final ContentResolver contentResolver) {
        try (final InputStream fis = contentResolver.openInputStream(uri)) {
            if (fis == null)
                return null;

            return calculateXxh3(fis);
        } catch (final IOException ex) {
            return null;
        }
    }

    /**
     * Calculate the XXH3-64 hash of the data in the specified InputStream
     *
     * @param inputStream The InputStream that should be hashed
     * @return The String object that contains the XXH3-64 hash of the stream
     * @throws IOException If the stream could not be read
     */
    static String calculateXxh3(final InputStream inputStream) throws IOException {
        final HashStream64 stream = Hashing.xxh3_64().hashStream();
        final byte[] dataBytes = new byte[8192];

        int nread;
        while ((nread = inputStream.read(dataBytes)) != -1) {
            stream.putBytes(dataBytes, 0, nread);
        }

        return String.format(Locale.ROOT, "%016x", stream.getAsLong());
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

    /**
     * Calculate the CRC32 value of a specified file
     *
     * @param uri             The Uri of the file that should be hashed
     * @param contentResolver The ContentResolver that should be used to open the file
     * @return The String object that represents the CRC32 value of the given file
     */
    public static String calculateCRC32(final Uri uri, final ContentResolver contentResolver) {
        try {
            final CRC32 crc = new CRC32();
            try (final InputStream fis = contentResolver.openInputStream(uri)) {
                if (fis == null)
                    return null;

                final byte[] dataBytes = new byte[1024];

                int nread;
                while ((nread = fis.read(dataBytes)) != -1) {
                    crc.update(dataBytes, 0, nread);
                }

                return Long.toHexString(crc.getValue());
            }
        } catch (final IOException ex) {
            return null;
        }
    }
}
