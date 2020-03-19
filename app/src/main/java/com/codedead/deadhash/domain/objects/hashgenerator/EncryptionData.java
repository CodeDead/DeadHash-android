package com.codedead.deadhash.domain.objects.hashgenerator;

import android.os.Parcel;
import android.os.Parcelable;

public class EncryptionData implements Parcelable {

    private final String encryptionName;
    private final String encryptionData;

    private final String compareCheck;

    EncryptionData(final String encryptionName, final String encryptionData, final String compareCheck) {
        if (encryptionName == null)
            throw new NullPointerException("Encryption name cannot be null!");
        if (encryptionData == null)
            throw new NullPointerException("Encryption data cannot be null!");
        if (encryptionName.length() == 0)
            throw new IllegalArgumentException("Encryption name cannot be empty!");
        if (encryptionData.length() == 0)
            throw new IllegalArgumentException("Encryption data cannot be empty!");

        this.encryptionName = encryptionName;
        this.encryptionData = encryptionData;

        this.compareCheck = compareCheck;
    }

    private EncryptionData(final Parcel in) {
        if (in == null) throw new NullPointerException("Parcel cannot be null!");

        encryptionName = in.readString();
        encryptionData = in.readString();
        compareCheck = in.readString();
    }

    public String getEncryptionName() {
        return encryptionName;
    }

    public String getEncryptionData() {
        return encryptionData;
    }

    public String getCompareCheck() {
        return compareCheck;
    }

    public static final Creator<EncryptionData> CREATOR = new Creator<EncryptionData>() {
        @Override
        public EncryptionData createFromParcel(final Parcel in) {
            return new EncryptionData(in);
        }

        @Override
        public EncryptionData[] newArray(final int size) {
            return new EncryptionData[size];
        }
    };


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(final Parcel dest, final int flags) {
        dest.writeString(encryptionName);
        dest.writeString(encryptionData);
        dest.writeString(compareCheck);
    }
}
