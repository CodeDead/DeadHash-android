package com.codedead.deadhash.domain.interfaces.hashgenerator;

import com.codedead.deadhash.domain.objects.hashgenerator.EncryptionData;

import java.util.List;

public interface IHashResponse {
    void hashDataFile(List<EncryptionData> data);

    void hashDataText(List<EncryptionData> data);
}
