package com.codedead.deadline.deadhash.domain;

import android.os.AsyncTask;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public abstract class HashGenerator extends AsyncTask<Void, Void, List<EncryptionData>> {

    private byte[] data;

    private boolean md5;
    private boolean sha1;
    private boolean sha224;
    private boolean sha256;
    private boolean sha384;
    private boolean sha512;
    private boolean crc32;

    private String compare;
    private List<EncryptionData> encryptionData;

    public HashResponse delegate = null;

    HashGenerator(byte[] data, boolean md5, boolean sha1, boolean sha224, boolean sha256, boolean sha384, boolean sha512, boolean crc32, String compare) {
        encryptionData = new ArrayList<>();
        this.data = data;

        this.md5 = md5;
        this.sha1 = sha1;
        this.sha224 = sha224;
        this.sha256 = sha256;
        this.sha384 = sha384;
        this.sha512 = sha512;
        this.crc32 = crc32;
        this.compare = compare;
    }

    HashGenerator(File data, boolean md5, boolean sha1, boolean sha224, boolean sha256, boolean sha384, boolean sha512, boolean crc32, String compare) throws IOException {
        encryptionData = new ArrayList<>();
        this.data = fullyReadFileToBytes(data);

        this.md5 = md5;
        this.sha1 = sha1;
        this.sha224 = sha224;
        this.sha256 = sha256;
        this.sha384 = sha384;
        this.sha512 = sha512;
        this.crc32 = crc32;
        this.compare = compare;
    }

    private byte[] fullyReadFileToBytes(File f) throws IOException {
        int size = (int) f.length();
        byte bytes[] = new byte[size];
        byte tmpBuff[] = new byte[size];
        try (FileInputStream fis = new FileInputStream(f)) {
            int read = fis.read(bytes, 0, size);
            if (read < size) {
                int remain = size - read;
                while (remain > 0) {
                    read = fis.read(tmpBuff, 0, remain);
                    System.arraycopy(tmpBuff, 0, bytes, size - remain, read);
                    remain -= read;
                }
            }
        }

        return bytes;
    }

    @Override
    protected List<EncryptionData> doInBackground(Void... params) {

        if (md5) {
            String md5 = HashService.calculateHash(getData(), "MD5");
            encryptionData.add(new EncryptionData("MD5", md5, compare));
        }

        if (sha1) {
            String sha1 = HashService.calculateHash(getData(), "SHA-1");
            encryptionData.add(new EncryptionData("SHA-1", sha1, compare));
        }

        if (sha224) {
            String sha224 = HashService.calculateHash(getData(), "SHA-224");
            encryptionData.add(new EncryptionData("SHA-224", sha224, compare));
        }

        if (sha256) {
            String sha256 = HashService.calculateHash(getData(), "SHA-256");
            encryptionData.add(new EncryptionData("SHA-256", sha256, compare));
        }

        if (sha384) {
            String sha384 = HashService.calculateHash(getData(), "SHA-384");
            encryptionData.add(new EncryptionData("SHA-384", sha384, compare));
        }

        if (sha512) {
            String sha512 = HashService.calculateHash(getData(), "SHA-512");
            encryptionData.add(new EncryptionData("SHA-512", sha512, compare));
        }

        if (crc32) {
            String crc32 = HashService.calculateCRC32(getData());
            encryptionData.add(new EncryptionData("CRC32", crc32, compare));
        }

        return encryptionData;
    }

    public byte[] getData() {
        return data;
    }
}
