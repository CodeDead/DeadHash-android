package com.codedead.deadline.deadhash.domain;

public class FileData {

    private String encryption_name;
    private String encryption_data;

    public FileData(String encryption_name, String encryption_data) {
        this.encryption_name = encryption_name;
        this.encryption_data = encryption_data;
    }

    String getEncryption_name() {
        return encryption_name;
    }

    String getEncryption_data() {
        return encryption_data;
    }
}
