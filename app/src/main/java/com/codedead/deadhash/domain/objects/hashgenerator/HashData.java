package com.codedead.deadhash.domain.objects.hashgenerator;

import android.os.Parcel;
import android.os.Parcelable;

public class HashData implements Parcelable {

    private final String hashName;
    private final String hashData;

    private final String compareCheck;

    public static final Creator<HashData> CREATOR = new Creator<HashData>() {
        @Override
        public HashData createFromParcel(final Parcel in) {
            return new HashData(in);
        }

        @Override
        public HashData[] newArray(final int size) {
            return new HashData[size];
        }
    };

    /**
     * Initialize a new HashData
     *
     * @param hashName     The name of the hashing algorithm
     * @param hashData     The content of the hash
     * @param compareCheck The hash that can be used to perform comparisons
     */
    HashData(final String hashName, final String hashData, final String compareCheck) {
        if (hashName == null)
            throw new NullPointerException("Hash name cannot be null!");
        if (hashData == null)
            throw new NullPointerException("Hash data cannot be null!");
        if (hashName.length() == 0)
            throw new IllegalArgumentException("Hash name cannot be empty!");
        if (hashData.length() == 0)
            throw new IllegalArgumentException("Hash data cannot be empty!");

        this.hashName = hashName;
        this.hashData = hashData;

        this.compareCheck = compareCheck;
    }

    private HashData(final Parcel in) {
        if (in == null) throw new NullPointerException("Parcel cannot be null!");

        hashName = in.readString();
        hashData = in.readString();
        compareCheck = in.readString();
    }

    /**
     * Get the name of the hashing algorithm
     *
     * @return The name of the hashing algorithm
     */
    public String getHashName() {
        return hashName;
    }

    /**
     * Get the hash
     *
     * @return The hash
     */
    public String getHashData() {
        return hashData;
    }

    /**
     * Get the hash that can be used to perform comparisons
     *
     * @return The hash that can be used to perform comparisons
     */
    public String getCompareCheck() {
        return compareCheck;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(final Parcel dest, final int flags) {
        dest.writeString(hashName);
        dest.writeString(hashData);
        dest.writeString(compareCheck);
    }
}
