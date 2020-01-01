package com.codedead.deadhash.domain;

import java.util.List;

public interface HashResponse {
    void hashDataFile(List<EncryptionData> data);

    void hashDataText(List<EncryptionData> data);
}
