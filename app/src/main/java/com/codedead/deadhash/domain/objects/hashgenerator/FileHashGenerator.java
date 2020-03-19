package com.codedead.deadhash.domain.objects.hashgenerator;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class FileHashGenerator extends HashGenerator {

    public FileHashGenerator(final File file, List<HashAlgorithm> hashAlgorithms, final String compare) throws IOException {
        super(file, hashAlgorithms, compare);
    }

    @Override
    protected void onPostExecute(List<EncryptionData> data) {
        hashResponse.hashDataFile(data);
    }
}
