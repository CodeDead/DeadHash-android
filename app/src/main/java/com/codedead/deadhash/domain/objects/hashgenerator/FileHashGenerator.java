package com.codedead.deadhash.domain.objects.hashgenerator;

import java.io.File;
import java.io.IOException;
import java.util.List;

public final class FileHashGenerator extends HashGenerator {

    /**
     * Initialize a new FileHashGenerator
     *
     * @param file           The File that should be used to calculate hashes
     * @param hashAlgorithms The List of HashingAlgorithm enums that should be used to calculate hashes
     * @param compare        The compare String for the calculated hashes
     * @throws IOException When the file could not be read
     */
    public FileHashGenerator(final File file, final List<HashAlgorithm> hashAlgorithms, final String compare) throws IOException {
        super(file, hashAlgorithms, compare);
    }

    @Override
    protected void onPostExecute(final List<HashData> data) {
        hashResponse.hashDataFile(data);
    }
}
