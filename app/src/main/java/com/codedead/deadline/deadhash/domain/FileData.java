package com.codedead.deadline.deadhash.domain;

public class FileData {

    private String encryption_name;
    private String encryption_data;

    public FileData(String encryption_name, String encryption_data) {
        this.encryption_name = encryption_name;
        this.encryption_data = encryption_data;
    }

    public String getEncryption_name() {
        return encryption_name;
    }

    public void setEncryption_name(String encryption_name) {
        this.encryption_name = encryption_name;
    }

    public String getEncryption_data() {
        return encryption_data;
    }

    public void setEncryption_data(String encryption_data) {
        this.encryption_data = encryption_data;
    }
}
