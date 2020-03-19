package com.codedead.deadhash.domain.objects.hashgenerator;

import java.util.List;

public final class TextHashGenerator extends HashGenerator {

    /**
     * Initialize a new TextHashGenerator
     *
     * @param data           The byte array that should be hashed
     * @param hashAlgorithms The List of HashingAlgorithm enums that should be used to calculate hashes
     * @param compare        The compare String for the calculated hashes
     */
    public TextHashGenerator(final byte[] data, final List<HashAlgorithm> hashAlgorithms, final String compare) {
        super(data, hashAlgorithms, compare);
    }

    @Override
    protected void onPostExecute(final List<HashData> datas) {
        hashResponse.hashDataText(datas);
    }
}
