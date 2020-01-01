package com.codedead.deadhash.domain;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class FileHashGenerator extends HashGenerator {

    public FileHashGenerator(File file, boolean md5, boolean sha1, boolean sha224, boolean sha256, boolean sha384, boolean sha512, boolean crc32, String compare) throws IOException {
        super(file, md5, sha1, sha224, sha256, sha384, sha512, crc32, compare);
    }

    @Override
    protected void onPostExecute(List<EncryptionData> data) {
        delegate.hashDataFile(data);
    }
}
