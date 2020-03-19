package com.codedead.deadhash.domain.objects.hashgenerator;

import android.os.AsyncTask;

import com.codedead.deadhash.domain.interfaces.hashgenerator.IHashResponse;
import com.codedead.deadhash.domain.utils.HashUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public abstract class HashGenerator extends AsyncTask<Void, Void, List<HashData>> {

    private byte[] data;
    private final List<HashAlgorithm> hashAlgorithms;
    private final List<HashData> hashData;
    private String compare;

    public IHashResponse hashResponse = null;

    /**
     * Initialize a new HashGenerator
     *
     * @param data           The byte array that should be hashed
     * @param hashAlgorithms The List of HashingAlgorithm enums that should be used to calculate hashes
     * @param compare        The compare String for the calculated hashes
     */
    HashGenerator(final byte[] data, final List<HashAlgorithm> hashAlgorithms, final String compare) {
        hashData = new ArrayList<>();
        this.data = data;

        this.hashAlgorithms = hashAlgorithms;
        this.compare = compare;
    }

    /**
     * Initialize a new HashGenerator
     *
     * @param data           The byte array that should be hashed
     * @param hashAlgorithms The List of HashingAlgorithm enums that should be used to calculate hashes
     * @param compare        The compare String for the calculated hashes
     * @throws IOException When the File could not be read
     */
    HashGenerator(final File data, final List<HashAlgorithm> hashAlgorithms, final String compare) throws IOException {
        hashData = new ArrayList<>();
        this.data = readFileToBytes(data);
        this.hashAlgorithms = hashAlgorithms;
        this.compare = compare;
    }

    /**
     * Read a file and return a byte array that represents the given File
     *
     * @param file The File that should be read
     * @return The byte array that represents the given File
     * @throws IOException When the File could not be read
     */
    private byte[] readFileToBytes(final File file) throws IOException {
        final int size = (int) file.length();
        final byte[] bytes = new byte[size];
        final byte[] tmpBuff = new byte[size];
        try (final FileInputStream fis = new FileInputStream(file)) {
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
    protected List<HashData> doInBackground(Void... params) {
        for (final HashAlgorithm algorithm : hashAlgorithms) {
            switch (algorithm) {
                case md5:
                    final String md5 = HashUtil.calculateHash(data, "MD5");
                    hashData.add(new HashData("MD5", md5, compare));
                    break;
                case sha1:
                    final String sha1 = HashUtil.calculateHash(data, "SHA-1");
                    hashData.add(new HashData("SHA-1", sha1, compare));
                    break;
                case sha224:
                    final String sha224 = HashUtil.calculateHash(data, "SHA-224");
                    hashData.add(new HashData("SHA-224", sha224, compare));
                    break;
                case sha256:
                    final String sha256 = HashUtil.calculateHash(data, "SHA-256");
                    hashData.add(new HashData("SHA-256", sha256, compare));
                    break;
                case sha384:
                    final String sha384 = HashUtil.calculateHash(data, "SHA-384");
                    hashData.add(new HashData("SHA-384", sha384, compare));
                    break;
                case sha512:
                    final String sha512 = HashUtil.calculateHash(data, "SHA-512");
                    hashData.add(new HashData("SHA-512", sha512, compare));
                    break;
                case crc32:
                    final String crc32 = HashUtil.calculateCRC32(data);
                    hashData.add(new HashData("CRC32", crc32, compare));
                    break;
            }
        }

        return hashData;
    }
}
