package com.codedead.deadhash.domain.objects.settings;

import android.content.Context;
import android.content.SharedPreferences;

import com.codedead.deadhash.R;

public class SettingsContainer {

    private String languageCode;
    private boolean calculateMd5;
    private boolean calculateSha1;
    private boolean calculateSha224;
    private boolean calculateSha256;
    private boolean calculateSha384;
    private boolean calculateSha512;
    private boolean calculateCrc32;
    private int reviewTimes;
    private int theme;

    /**
     * Initialize a new SettingsContainer
     */
    public SettingsContainer() {
        // Default constructor
    }

    /**
     * Get the language code
     *
     * @return The language code
     */
    public String getLanguageCode() {
        return languageCode;
    }

    /**
     * Set the language code
     *
     * @param languageCode The language code
     */
    public void setLanguageCode(final String languageCode) {
        this.languageCode = languageCode;
    }

    /**
     * Get whether MD5 hashes should be calculated
     *
     * @return True if MD5 hashes should be calculated, otherwise false
     */
    public boolean isCalculateMd5() {
        return calculateMd5;
    }

    /**
     * Set whether MD5 hashes should be calculated
     *
     * @param calculateMd5 True if MD5 hashes should be calculated, otherwise false
     */
    public void setCalculateMd5(final boolean calculateMd5) {
        this.calculateMd5 = calculateMd5;
    }

    /**
     * Get whether SHA1 hashes should be calculated
     *
     * @return True if SHA1 hashes should be calculated, otherwise false
     */
    public boolean isCalculateSha1() {
        return calculateSha1;
    }

    /**
     * Set whether SHA1 hashes should be calculated
     *
     * @param calculateSha1 True if SHA1 hashes should be calculated, otherwise false
     */
    public void setCalculateSha1(final boolean calculateSha1) {
        this.calculateSha1 = calculateSha1;
    }

    /**
     * Get whether SHA224 hashes should be calculated
     *
     * @return True if SHA224 hashes should be calculated, otherwise false
     */
    public boolean isCalculateSha224() {
        return calculateSha224;
    }

    /**
     * Set whether SHA224 hashes should be calculated
     *
     * @param calculateSha224 True if SHA224 hashes should be calculated, otherwise false
     */
    public void setCalculateSha224(final boolean calculateSha224) {
        this.calculateSha224 = calculateSha224;
    }

    /**
     * Get whether SHA256 hashes should be calculated
     *
     * @return True if SHA256 hashes should be calculated, otherwise false
     */
    public boolean isCalculateSha256() {
        return calculateSha256;
    }

    /**
     * Set whether SHA256 hashes should be calculated
     *
     * @param calculateSha256 True if SHA256 hashes should be calculated, otherwise false
     */
    public void setCalculateSha256(final boolean calculateSha256) {
        this.calculateSha256 = calculateSha256;
    }

    /**
     * Get whether SHA384 hashes should be calculated
     *
     * @return True if SHA384 hashes should be calculated, otherwise false
     */
    public boolean isCalculateSha384() {
        return calculateSha384;
    }

    /**
     * Set whether SHA384 hashes should be calculated
     *
     * @param calculateSha384 True if SHA384 hashes should be calculated, otherwise false
     */
    public void setCalculateSha384(final boolean calculateSha384) {
        this.calculateSha384 = calculateSha384;
    }

    /**
     * Get whether SHA512 hashes should be calculated
     *
     * @return True if SHA512 hashes should be calculated, otherwise false
     */
    public boolean isCalculateSha512() {
        return calculateSha512;
    }

    /**
     * Set whether SHA512 hashes should be calculated
     *
     * @param calculateSha512 True if SHA512 hashes should be calculated, otherwise false
     */
    public void setCalculateSha512(final boolean calculateSha512) {
        this.calculateSha512 = calculateSha512;
    }

    /**
     * Get whether CRC32 values should be calculated
     *
     * @return True if CRC32 values should be calculated, otherwise false
     */
    public boolean isCalculateCrc32() {
        return calculateCrc32;
    }

    /**
     * Set whether CRC32 values should be calculated
     *
     * @param calculateCrc32 True if CRC32 values should be calculated, otherwise false
     */
    public void setCalculateCrc32(final boolean calculateCrc32) {
        this.calculateCrc32 = calculateCrc32;
    }

    /**
     * Get the amount of times a user has been asked to review the application
     *
     * @return The amount of times a user has been asked to review the application
     */
    public int getReviewTimes() {
        return reviewTimes;
    }

    /**
     * Set the amount of times a user has been asked to review the application
     *
     * @param reviewTimes The amount of times a user has been asked to review the application
     */
    public void setReviewTimes(final int reviewTimes) {
        if (reviewTimes < 0)
            throw new IllegalArgumentException("reviewTimes cannot be smaller than zero!");

        this.reviewTimes = reviewTimes;
    }

    /**
     * Get the theme index
     *
     * @return The theme index
     */
    public int getTheme() {
        return theme;
    }

    /**
     * Set the theme index
     *
     * @param theme The theme index
     */
    public void setTheme(final int theme) {
        this.theme = theme;
    }

    /**
     * Load the settings
     *
     * @param context The Context that can be used to load the settings
     */
    public void loadSettings(final Context context) {
        if (context == null) throw new NullPointerException("Context cannot be null!");

        final SharedPreferences sharedPreferences = context.getSharedPreferences(context.getString(R.string.preferences_file_key), Context.MODE_PRIVATE);

        languageCode = sharedPreferences.getString("language", "en");
        calculateMd5 = sharedPreferences.getBoolean("md5", true);
        calculateSha1 = sharedPreferences.getBoolean("sha1", true);
        calculateSha224 = sharedPreferences.getBoolean("sha224", true);
        calculateSha256 = sharedPreferences.getBoolean("sha256", true);
        calculateSha384 = sharedPreferences.getBoolean("sha384", true);
        calculateSha512 = sharedPreferences.getBoolean("sha512", true);
        calculateCrc32 = sharedPreferences.getBoolean("crc32", true);
        reviewTimes = sharedPreferences.getInt("reviewTimes", 0);
        theme = sharedPreferences.getInt("theme", 0);
    }

    /**
     * Save the settings
     *
     * @param context The Context that can be used to save the settings
     */
    public void saveSettings(final Context context) {
        if (context == null) throw new NullPointerException("Context cannot be null!");

        final SharedPreferences sharedPreferences = context.getSharedPreferences(context.getString(R.string.preferences_file_key), Context.MODE_PRIVATE);
        final SharedPreferences.Editor edit = sharedPreferences.edit();

        edit.putString("language", getLanguageCode());
        edit.putBoolean("md5", isCalculateMd5());
        edit.putBoolean("sha1", isCalculateSha1());
        edit.putBoolean("sha224", isCalculateSha224());
        edit.putBoolean("sha256", isCalculateSha256());
        edit.putBoolean("sha384", isCalculateSha384());
        edit.putBoolean("sha512", isCalculateSha512());
        edit.putBoolean("crc32", isCalculateCrc32());
        edit.putInt("reviewTimes", getReviewTimes());
        edit.putInt("theme", getTheme());

        edit.apply();
    }
}
