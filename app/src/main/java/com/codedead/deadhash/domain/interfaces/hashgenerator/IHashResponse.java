package com.codedead.deadhash.domain.interfaces.hashgenerator;

import com.codedead.deadhash.domain.objects.hashgenerator.HashData;

import java.util.List;

public interface IHashResponse {
    void hashDataFile(List<HashData> data);

    void hashDataText(List<HashData> data);
}
