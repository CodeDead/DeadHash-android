package com.codedead.deadhash.domain.objects.hashgenerator;

import com.codedead.deadhash.domain.utils.HashUtil;

import java.util.List;

public final class TextHashGenerator extends HashGenerator {

    private final String data;

    /**
     * Initialize a new TextHashGenerator
     *
     * @param data           The String that should be hashed
     * @param hashAlgorithms The List of HashingAlgorithm enums that should be used to calculate hashes
     * @param compare        The compare String for the calculated hashes
     */
    public TextHashGenerator(final String data, List<HashAlgorithm> hashAlgorithms, String compare) {
        super(hashAlgorithms, compare);

        if (data == null)
            throw new NullPointerException("Data cannot be null!");
        if (data.isEmpty())
            throw new IllegalArgumentException("Data cannot be empty!");

        this.data = data;
    }

    /**
     * Generate the List of HashData for the given input data
     *
     * @return The List of HashData for the given input data
     */
    @Override
    public List<HashData> generateHashes() {
        for (final HashAlgorithm algorithm : super.getHashAlgorithms()) {
            switch (algorithm) {
                case md5 -> {
                    final String md5 = HashUtil.calculateHash(data.getBytes(), "MD5");
                    getHashData().add(new HashData("MD5", md5, getCompare()));
                }
                case sha1 -> {
                    final String sha1 = HashUtil.calculateHash(data.getBytes(), "SHA-1");
                    getHashData().add(new HashData("SHA-1", sha1, getCompare()));
                }
                case sha224 -> {
                    final String sha224 = HashUtil.calculateHash(data.getBytes(), "SHA-224");
                    getHashData().add(new HashData("SHA-224", sha224, getCompare()));
                }
                case sha256 -> {
                    final String sha256 = HashUtil.calculateHash(data.getBytes(), "SHA-256");
                    getHashData().add(new HashData("SHA-256", sha256, getCompare()));
                }
                case sha384 -> {
                    final String sha384 = HashUtil.calculateHash(data.getBytes(), "SHA-384");
                    getHashData().add(new HashData("SHA-384", sha384, getCompare()));
                }
                case sha512 -> {
                    final String sha512 = HashUtil.calculateHash(data.getBytes(), "SHA-512");
                    getHashData().add(new HashData("SHA-512", sha512, getCompare()));
                }
                case crc32 -> {
                    final String crc32 = HashUtil.calculateCRC32(data.getBytes());
                    getHashData().add(new HashData("CRC32", crc32, getCompare()));
                }
            }
        }

        return getHashData();
    }
}
