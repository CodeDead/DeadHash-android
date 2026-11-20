package com.codedead.deadhash.gui;

import static android.content.pm.PackageManager.GET_META_DATA;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import com.codedead.deadhash.R;
import com.codedead.deadhash.domain.utils.HashUtil;
import com.codedead.deadhash.domain.utils.IntentUtils;
import com.codedead.deadhash.domain.utils.LocaleHelper;

public class SettingsActivity extends AppCompatActivity {

    private boolean resettingSettings;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        sharedPreferences.registerOnSharedPreferenceChangeListener(listener);
        LocaleHelper.setLocale(this, sharedPreferences.getString("language", "en"));

        resetTitle();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);

        final Toolbar toolbar = findViewById(R.id.toolbar_settings);
        setSupportActionBar(toolbar);

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.settings, new SettingsFragment())
                    .commit();
        }

        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private final SharedPreferences.OnSharedPreferenceChangeListener listener = (prefs, key) -> {
        if (key == null || resettingSettings)
            return;

        if (key.equals("language")) {
            LocaleHelper.setLocale(getApplicationContext(), prefs.getString("language", "en"));
            recreate();
        } else if (key.equals("theme")) {
            final String theme = prefs.getString("theme", "0");
            switch (theme) {
                case "0" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                case "1" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                case "2" ->
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
            }
        }
    };

    /**
     * Restore all settings to their default values and apply the default locale and theme
     */
    private void resetSettings() {
        final Context appContext = getApplicationContext();

        resettingSettings = true;
        try {
            PreferenceManager.getDefaultSharedPreferences(appContext).edit().clear().apply();
            appContext.getSharedPreferences(getString(R.string.preferences_file_key), Context.MODE_PRIVATE).edit().clear().apply();

            LocaleHelper.setLocale(appContext, "en");
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        } finally {
            resettingSettings = false;
        }

        recreate();
    }

    /**
     * Reset the title of the activity
     */
    private void resetTitle() {
        try {
            final int label = getPackageManager().getActivityInfo(getComponentName(), GET_META_DATA).labelRes;
            if (label != 0) {
                setTitle(label);
            }
        } catch (final PackageManager.NameNotFoundException ex) {
            IntentUtils.showAlert(this, ex.getMessage());
        }
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConfigurationChanged(@NonNull final Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        LocaleHelper.onAttach(getBaseContext());
    }

    @Override
    protected void attachBaseContext(final Context base) {
        super.attachBaseContext(LocaleHelper.onAttach(base));
    }

    public static class SettingsFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(final Bundle savedInstanceState, final String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);

            if (!HashUtil.isXxh3Supported()) {
                final Preference xxh3Preference = findPreference("xxh3");
                if (xxh3Preference != null) {
                    xxh3Preference.setVisible(false);
                }
            }

            final Preference resetPreference = findPreference("reset_settings");
            if (resetPreference != null) {
                resetPreference.setOnPreferenceClickListener(preference -> {
                    showResetSettingsDialog();
                    return true;
                });
            }
        }

        /**
         * Show a confirmation dialog before resetting all settings
         */
        private void showResetSettingsDialog() {
            new AlertDialog.Builder(requireContext())
                    .setTitle(R.string.alert_reset_settings_title)
                    .setMessage(R.string.alert_reset_settings_message)
                    .setCancelable(true)
                    .setPositiveButton(R.string.reset, (dialog, which) -> ((SettingsActivity) requireActivity()).resetSettings())
                    .setNegativeButton(android.R.string.cancel, (dialog, which) -> dialog.cancel())
                    .show();
        }
    }
}
