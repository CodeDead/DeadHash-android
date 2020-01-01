package com.codedead.deadhash.domain;

import java.util.List;

public class TextHashGenerator extends HashGenerator {

    public TextHashGenerator(byte[] data, boolean md5, boolean sha1, boolean sha224, boolean sha256, boolean sha384, boolean sha512, boolean crc32, String compare) {
        super(data, md5, sha1, sha224, sha256, sha384, sha512, crc32, compare);
    }

    @Override
    protected void onPostExecute(List<EncryptionData> datas) {
        delegate.hashDataText(datas);
    }
}
