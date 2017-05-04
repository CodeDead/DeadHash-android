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
            int halfbyte = (b >>> 4) & 0x0F;
            int two_halfs = 0;
            do {
                buf.append((0 <= halfbyte) && (halfbyte <= 9) ? (char) ('0' + halfbyte) : (char) ('a' + (halfbyte - 10)));
                halfbyte = b & 0x0F;
            } while (two_halfs++ < 1);
        }
        return buf.toString();
    }

    public static String calculateHash(File fileName, String kind) {
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

    public static String calculateCRC32(File filePath) {
        try {
            InputStream inputStream = new BufferedInputStream(new FileInputStream(filePath));
            CRC32 crc = new CRC32();
            int cnt;

            while ((cnt = inputStream.read()) != -1) {
                crc.update(cnt);
            }
            return "" + crc.getValue();
        } catch (Exception ex) {
            return null;
        }
    }
}
