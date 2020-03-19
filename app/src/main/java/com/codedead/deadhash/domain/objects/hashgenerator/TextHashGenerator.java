package com.codedead.deadhash.domain.objects.hashgenerator;

import java.util.List;

public class TextHashGenerator extends HashGenerator {

    public TextHashGenerator(byte[] data, List<HashAlgorithm> hashAlgorithms, String compare) {
        super(data, hashAlgorithms, compare);
    }

    @Override
    protected void onPostExecute(List<EncryptionData> datas) {
        hashResponse.hashDataText(datas);
    }
}
