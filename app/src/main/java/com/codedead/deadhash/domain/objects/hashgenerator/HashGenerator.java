package com.codedead.deadhash.domain.objects.hashgenerator;

import com.codedead.deadhash.domain.utils.HashUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public final class HashGenerator {

    private final byte[] data;
    private final List<HashAlgorithm> hashAlgorithms;
    private final List<HashData> hashData;
    private final String compare;

    /**
     * Initialize a new HashGenerator
     *
     * @param data           The byte array that should be hashed
     * @param hashAlgorithms The List of HashingAlgorithm enums that should be used to calculate hashes
     * @param compare        The compare String for the calculated hashes
     */
    public HashGenerator(final byte[] data, final List<HashAlgorithm> hashAlgorithms, final String compare) {
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
    public HashGenerator(final File data, final List<HashAlgorithm> hashAlgorithms, final String compare) throws IOException {
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
        if (file == null)
            throw new NullPointerException("File cannot be null!");

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

    /**
     * Generate the List of HashData for the given input data
     * @return The List of HashData for the given input data
     */
    public List<HashData> generateHashes() {
        for (final HashAlgorithm algorithm : hashAlgorithms) {
            switch (algorithm) {
                case md5 -> {
                    final String md5 = HashUtil.calculateHash(data, "MD5");
                    hashData.add(new HashData("MD5", md5, compare));
                }
                case sha1 -> {
                    final String sha1 = HashUtil.calculateHash(data, "SHA-1");
                    hashData.add(new HashData("SHA-1", sha1, compare));
                }
                case sha224 -> {
                    final String sha224 = HashUtil.calculateHash(data, "SHA-224");
                    hashData.add(new HashData("SHA-224", sha224, compare));
                }
                case sha256 -> {
                    final String sha256 = HashUtil.calculateHash(data, "SHA-256");
                    hashData.add(new HashData("SHA-256", sha256, compare));
                }
                case sha384 -> {
                    final String sha384 = HashUtil.calculateHash(data, "SHA-384");
                    hashData.add(new HashData("SHA-384", sha384, compare));
                }
                case sha512 -> {
                    final String sha512 = HashUtil.calculateHash(data, "SHA-512");
                    hashData.add(new HashData("SHA-512", sha512, compare));
                }
                case crc32 -> {
                    final String crc32 = HashUtil.calculateCRC32(data);
                    hashData.add(new HashData("CRC32", crc32, compare));
                }
            }
        }

        return hashData;
    }
}
