package com.codedead.deadhash.domain.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;

import androidx.preference.PreferenceManager;

import java.util.Locale;

public final class LocaleHelper {

    /**
     * Method that should be called when a Context is attached
     *
     * @param context The Context that is attached
     * @return The Context that contains the correct locale
     */
    public static Context onAttach(final Context context) {
        final String lang = getPersistedData(context, Locale.getDefault().getLanguage());
        return setLocale(context, lang);
    }

    /**
     * Method that should be called when a Context is attached
     *
     * @param context         The Context that is attached
     * @param defaultLanguage The default language
     * @return THe Context that contains the correct locale
     */
    public static Context onAttach(final Context context, final String defaultLanguage) {
        final String lang = getPersistedData(context, defaultLanguage);
        return setLocale(context, lang);
    }

    /**
     * Set the locale of a Context
     *
     * @param context  The Context for which the locale should be set
     * @param language The Language of the new locale
     * @return The Context that contains the correct locale
     */
    public static Context setLocale(final Context context, final String language) {
        persist(context, language);
        return updateResourcesLegacy(context, language);
    }

    /**
     * Get the persisted language code
     *
     * @param context         The Context for which the persisted language could should be retrieved
     * @param defaultLanguage The default language code
     * @return The String that contains the persisted language code
     */
    private static String getPersistedData(final Context context, final String defaultLanguage) {
        final SharedPreferences preferences = context.getSharedPreferences("deadhashsettings", Context.MODE_PRIVATE);
        return preferences.getString("language", defaultLanguage);
    }

    /**
     * Persist the language code
     *
     * @param context  The Context that can be used to persist the data
     * @param language The language code that should be persisted
     */
    private static void persist(final Context context, final String language) {
        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();

        editor.putString("language", language);
        editor.apply();
    }

    /**
     * Update the resources of a specific Context
     *
     * @param context  The Context that should be updated to contain the proper resources
     * @param language The language code that should be set
     * @return The Context that contains the correct resources and locale
     */
    private static Context updateResourcesLegacy(final Context context, final String language) {
        final Locale locale = new Locale(language);
        Locale.setDefault(locale);

        final Resources resources = context.getResources();

        final Configuration configuration = resources.getConfiguration();
        configuration.locale = locale;

        resources.updateConfiguration(configuration, resources.getDisplayMetrics());

        return context;
    }
}
