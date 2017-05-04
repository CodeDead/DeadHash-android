package com.codedead.deadline.deadhash.domain;

public class FileData {

    private String encryption_name;
    private String encryption_data;

    private String compareCheck;

    public FileData(String encryption_name, String encryption_data, String compareCheck) {
        this.encryption_name = encryption_name;
        this.encryption_data = encryption_data;

        this.compareCheck = compareCheck;
    }

    public String getCompareCheck() {
        return compareCheck;
    }

    String getEncryption_name() {
        return encryption_name;
    }

    String getEncryption_data() {
        return encryption_data;
    }
}
