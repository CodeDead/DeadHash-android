package com.codedead.deadhash.domain.objects.hashgenerator;

import android.content.ContentResolver;
import android.net.Uri;

import com.codedead.deadhash.domain.utils.HashUtil;

import java.util.List;

public final class FileHashGenerator extends HashGenerator {

    private final Uri uri;
    private final ContentResolver contentResolver;

    /**
     * Initialize a new HashGenerator
     *
     * @param uri            The Uri of the file that should be hashed
     * @param hashAlgorithms The List of HashingAlgorithm enums that should be used to calculate hashes
     * @param compare        The compare String for the calculated hashes
     */
    public FileHashGenerator(final Uri uri, final ContentResolver contentResolver, final List<HashAlgorithm> hashAlgorithms, final String compare) {
        super(hashAlgorithms, compare);

        if (uri == null)
            throw new NullPointerException("File cannot be null!");
        if (contentResolver == null)
            throw new NullPointerException("ContentResolver cannot be null!");

        this.uri = uri;
        this.contentResolver = contentResolver;
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
                    final String md5 = HashUtil.calculateHash(uri, contentResolver, "MD5");
                    getHashData().add(new HashData("MD5", md5, getCompare()));
                }
                case sha1 -> {
                    final String sha1 = HashUtil.calculateHash(uri, contentResolver, "SHA-1");
                    getHashData().add(new HashData("SHA-1", sha1, getCompare()));
                }
                case sha224 -> {
                    final String sha224 = HashUtil.calculateHash(uri, contentResolver, "SHA-224");
                    getHashData().add(new HashData("SHA-224", sha224, getCompare()));
                }
                case sha256 -> {
                    final String sha256 = HashUtil.calculateHash(uri, contentResolver, "SHA-256");
                    getHashData().add(new HashData("SHA-256", sha256, getCompare()));
                }
                case sha384 -> {
                    final String sha384 = HashUtil.calculateHash(uri, contentResolver, "SHA-384");
                    getHashData().add(new HashData("SHA-384", sha384, getCompare()));
                }
                case sha512 -> {
                    final String sha512 = HashUtil.calculateHash(uri, contentResolver, "SHA-512");
                    getHashData().add(new HashData("SHA-512", sha512, getCompare()));
                }
                case crc32 -> {
                    final String crc32 = HashUtil.calculateCRC32(uri, contentResolver);
                    getHashData().add(new HashData("CRC32", crc32, getCompare()));
                }
            }
        }

        return getHashData();
    }
}
