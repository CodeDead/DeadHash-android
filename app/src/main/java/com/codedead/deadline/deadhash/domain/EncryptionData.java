package com.codedead.deadline.deadhash.domain;

import android.os.Parcel;
import android.os.Parcelable;

public class EncryptionData implements Parcelable {

    private final String encryption_name;
    private final String encryption_data;

    private final String compareCheck;

    EncryptionData(String encryption_name, String encryption_data, String compareCheck) {
        this.encryption_name = encryption_name;
        this.encryption_data = encryption_data;

        this.compareCheck = compareCheck;
    }

    private EncryptionData(Parcel in) {
        encryption_name = in.readString();
        encryption_data = in.readString();
        compareCheck = in.readString();
    }

    public static final Creator<EncryptionData> CREATOR = new Creator<EncryptionData>() {
        @Override
        public EncryptionData createFromParcel(Parcel in) {
            return new EncryptionData(in);
        }

        @Override
        public EncryptionData[] newArray(int size) {
            return new EncryptionData[size];
        }
    };

    String getCompareCheck() {
        return compareCheck;
    }

    String getEncryption_name() {
        return encryption_name;
    }

    String getEncryption_data() {
        return encryption_data;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(encryption_name);
        dest.writeString(encryption_data);
        dest.writeString(compareCheck);
    }
}
