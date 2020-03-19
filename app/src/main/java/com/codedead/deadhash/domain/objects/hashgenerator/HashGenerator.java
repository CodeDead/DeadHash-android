package com.codedead.deadhash.domain.objects.hashgenerator;

import android.os.AsyncTask;

import com.codedead.deadhash.domain.interfaces.hashgenerator.IHashResponse;
import com.codedead.deadhash.domain.utils.HashUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public abstract class HashGenerator extends AsyncTask<Void, Void, List<EncryptionData>> {

    private byte[] data;
    private final List<HashAlgorithm> hashAlgorithms;
    private final List<EncryptionData> encryptionData;
    private String compare;

    public IHashResponse hashResponse = null;

    HashGenerator(byte[] data, List<HashAlgorithm> hashAlgorithms, String compare) {
        encryptionData = new ArrayList<>();
        this.data = data;

        this.hashAlgorithms = hashAlgorithms;
        this.compare = compare;
    }

    HashGenerator(File data, List<HashAlgorithm> hashAlgorithms, String compare) throws IOException {
        encryptionData = new ArrayList<>();
        this.data = fullyReadFileToBytes(data);
        this.hashAlgorithms = hashAlgorithms;
        this.compare = compare;
    }

    private byte[] fullyReadFileToBytes(File f) throws IOException {
        final int size = (int) f.length();
        final byte[] bytes = new byte[size];
        final byte[] tmpBuff = new byte[size];
        try (final FileInputStream fis = new FileInputStream(f)) {
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
        for (HashAlgorithm algorithm : hashAlgorithms) {
            switch (algorithm) {
                case md5:
                    final String md5 = HashUtil.calculateHash(data, "MD5");
                    encryptionData.add(new EncryptionData("MD5", md5, compare));
                    break;
                case sha1:
                    final String sha1 = HashUtil.calculateHash(data, "SHA-1");
                    encryptionData.add(new EncryptionData("SHA-1", sha1, compare));
                    break;
                case sha224:
                    final String sha224 = HashUtil.calculateHash(data, "SHA-224");
                    encryptionData.add(new EncryptionData("SHA-224", sha224, compare));
                    break;
                case sha256:
                    final String sha256 = HashUtil.calculateHash(data, "SHA-256");
                    encryptionData.add(new EncryptionData("SHA-256", sha256, compare));
                    break;
                case sha384:
                    final String sha384 = HashUtil.calculateHash(data, "SHA-384");
                    encryptionData.add(new EncryptionData("SHA-384", sha384, compare));
                    break;
                case sha512:
                    final String sha512 = HashUtil.calculateHash(data, "SHA-512");
                    encryptionData.add(new EncryptionData("SHA-512", sha512, compare));
                    break;
                case crc32:
                    final String crc32 = HashUtil.calculateCRC32(data);
                    encryptionData.add(new EncryptionData("CRC32", crc32, compare));
                    break;
            }
        }

        return encryptionData;
    }
}
