package com.codedead.deadline.deadhash.domain;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.MessageDigest;
import java.util.zip.CRC32;

public final class HashService {

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

    public static String calculateStringHash(String text, String kind) {
        try {
            MessageDigest md = MessageDigest.getInstance(kind);
            byte[] textBytes = text.getBytes();
            md.update(textBytes, 0, textBytes.length);
            byte[] hash = md.digest();
            return convertToHex(hash);
        } catch (Exception ex) {
            return null;
        }
    }

    public static String calculateFileHash(File fileName, String kind) {
        try {
            MessageDigest digest = MessageDigest.getInstance(kind);
            InputStream fis = new FileInputStream(fileName);
            int n = 0;
            byte[] buffer = new byte[8192];
            while (n != -1) {
                n = fis.read(buffer);
                if (n > 0) {
                    digest.update(buffer, 0, n);
                }
            }

            return convertToHex(digest.digest());
        } catch (Exception e) {
            return null;
        }
    }

    public static String calculateStringCRC32(String data) {
        try {
            CRC32 crc = new CRC32();
            crc.update(data.getBytes());

            return Long.toHexString(crc.getValue());
        } catch (Exception ex) {
            return null;
        }
    }

    public static String calculateFileCRC32(File filePath) {
        try {
            InputStream inputStream = new BufferedInputStream(new FileInputStream(filePath));
            CRC32 crc = new CRC32();
            int cnt;

            while ((cnt = inputStream.read()) != -1) {
                crc.update(cnt);
            }
            return Long.toHexString(crc.getValue());
        } catch (Exception ex) {
            return null;
        }
    }
}
