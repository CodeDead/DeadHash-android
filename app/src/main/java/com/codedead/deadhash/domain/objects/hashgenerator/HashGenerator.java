package com.codedead.deadhash.domain.objects.hashgenerator;

import java.util.ArrayList;
import java.util.List;

public abstract class HashGenerator implements IHashGenerator {
    private final List<HashAlgorithm> hashAlgorithms;
    private final List<HashData> hashData;
    private final String compare;

    /**
     * Initialize a new HashGenerator
     *
     * @param hashAlgorithms The List of HashingAlgorithm enums that should be used to calculate hashes
     * @param compare        The compare String for the calculated hashes
     */
    public HashGenerator(final List<HashAlgorithm> hashAlgorithms, final String compare) {
        hashData = new ArrayList<>();
        this.hashAlgorithms = hashAlgorithms;
        this.compare = compare;
    }

    /**
     * Get the List of HashData for the given input data
     *
     * @return The List of HashData for the given input data
     */
    public List<HashAlgorithm> getHashAlgorithms() {
        return hashAlgorithms;
    }

    /**
     * Get the List of HashData for the given input data
     *
     * @return The List of HashData for the given input data
     */
    public List<HashData> getHashData() {
        return hashData;
    }

    /**
     * Get the compare String for the calculated hashes
     *
     * @return The compare String for the calculated hashes
     */
    public String getCompare() {
        return compare;
    }
}
