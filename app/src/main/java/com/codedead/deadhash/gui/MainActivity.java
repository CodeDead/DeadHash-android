package com.codedead.deadhash.gui;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ShareCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.codedead.deadhash.domain.objects.hashgenerator.HashAlgorithm;
import com.codedead.deadhash.domain.objects.settings.SettingsContainer;
import com.codedead.deadhash.domain.utils.IntentUtils;
import com.codedead.deadhash.domain.utils.StreamUtility;
import com.google.android.material.navigation.NavigationView;

import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Looper;
import android.text.method.LinkMovementMethod;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.codedead.deadhash.R;
import com.codedead.deadhash.domain.utils.DataAdapter;
import com.codedead.deadhash.domain.objects.hashgenerator.HashData;
import com.codedead.deadhash.domain.objects.hashgenerator.HashGenerator;
import com.codedead.deadhash.domain.utils.LocaleHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private boolean doubleBackToExitPressedOnce;

    private ViewFlipper viewFlipper;
    private EditText edtFileCompare;
    private EditText edtFilePath;
    private EditText edtTextCompare;
    private EditText edtTextData;

    private RecyclerView mRecyclerViewFile;
    private RecyclerView mRecyclerViewText;

    private ProgressBar pgbFile;
    private ProgressBar pgbText;

    private RecyclerView.LayoutManager mLayoutManagerFile;

    private ArrayList<HashData> fileDataArrayList = new ArrayList<>();
    private ArrayList<HashData> textDataArrayList = new ArrayList<>();

    private DataAdapter mAdapterFile = new DataAdapter(fileDataArrayList);
    private DataAdapter mAdapterText = new DataAdapter(textDataArrayList);

    private final SettingsContainer settingsContainer = new SettingsContainer();

    private boolean fileLoading;
    private boolean textLoading;

    private boolean paused;

    private final String tmpFile = "tmpFile";
    private String lastLanguage;

    private ActivityResultLauncher<Intent> activityResultLauncher;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        settingsContainer.loadSettings(getApplicationContext());
        LocaleHelper.setLocale(this, settingsContainer.getLanguageCode());
        lastLanguage = settingsContainer.getLanguageCode();

        loadTheme();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final DrawerLayout drawer = findViewById(R.id.drawer_layout);
        final ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        final NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        viewFlipper = findViewById(R.id.vf);

        if (savedInstanceState != null) {
            final int flipperPosition = savedInstanceState.getInt("TAB_NUMBER");
            viewFlipper.setDisplayedChild(flipperPosition);

            if (flipperPosition > 1) {
                navigationView.setCheckedItem(navigationView.getMenu().getItem(1).getSubMenu().getItem(flipperPosition - 2).getItemId());
            } else {
                navigationView.setCheckedItem(navigationView.getMenu().getItem(0).getSubMenu().getItem(flipperPosition).getItemId());
            }

            if (!savedInstanceState.getBoolean("KEEP_FILE")) {
                deleteTempFile();
            }
        } else {
            navigationView.setCheckedItem(navigationView.getMenu().getItem(0).getSubMenu().getItem(0).getItemId());
            deleteTempFile();
        }

        loadFileHashContent(savedInstanceState);
        loadTextHashContent(savedInstanceState);
        loadHelpContent();
        loadAboutContent();

        loadAlertContent();

        this.activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getData() != null) {
                        final Uri selectedFileUri = result.getData().getData();
                        if (selectedFileUri != null) {
                            try (final InputStream selectedFileStream = getContentResolver().openInputStream(selectedFileUri)) {
                                final File outputFile = new File(getApplicationContext().getCacheDir(), tmpFile);

                                try (final FileOutputStream outputStream = new FileOutputStream(outputFile, false)) {
                                    if (selectedFileStream != null) {
                                        StreamUtility.copyStream(selectedFileStream, outputStream);
                                        edtFilePath.setText(selectedFileUri.getPath());
                                    } else {
                                        Toast.makeText(getApplicationContext(), R.string.error_open_file, Toast.LENGTH_SHORT).show();
                                    }
                                } catch (final IOException ex) {
                                    Toast.makeText(getApplicationContext()
                                            , R.string.error_copy_file, Toast.LENGTH_SHORT).show();
                                }
                            } catch (final IOException ex) {
                                Toast.makeText(getApplicationContext(), R.string.error_open_file, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), R.string.error_open_file, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    /**
     * Load the current theme
     */
    private void loadTheme() {
        switch (settingsContainer.getTheme()) {
            case "0":
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                break;
            case "1":
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                break;
            case "2":
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        final MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.top_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        final int itemId = item.getItemId();
        if (itemId == R.id.nav_scan_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Delete the temporary file to save storage
     */
    private void deleteTempFile() {
        final File f = new File(getApplicationContext().getCacheDir(), tmpFile);
        if (f.exists()) {
            //noinspection ResultOfMethodCallIgnored
            f.delete();
        }
    }

    /**
     * Load the content and logic for AlertDialog objects
     */
    private void loadAlertContent() {
        if (settingsContainer.getReviewTimes() >= 2) return;

        final Random rnd = new Random();

        new CountDownTimer(rnd.nextInt(180) * 1000, 1000) {

            @Override
            public void onTick(long millisUntilFinished) {
            }

            @Override
            public void onFinish() {
                final AlertDialog.Builder reviewBuilder = new AlertDialog.Builder(MainActivity.this);
                reviewBuilder.setTitle(R.string.alert_review_title);
                reviewBuilder.setMessage(R.string.alert_review_text);
                reviewBuilder.setCancelable(true);

                reviewBuilder.setPositiveButton(android.R.string.ok, (dialog, id) -> {
                    dialog.cancel();

                    addReview(true);
                    IntentUtils.openPlayStore(MainActivity.this.getApplicationContext());
                });

                reviewBuilder.setNegativeButton(R.string.alert_review_never, (dialog, id) -> {
                    dialog.cancel();
                    addReview(true);
                });

                reviewBuilder.setNeutralButton(android.R.string.cancel, (dialog, which) -> {
                    dialog.cancel();
                    addReview(false);
                });

                final AlertDialog alert1 = reviewBuilder.create();
                if (!isFinishing() && !paused) {
                    alert1.show();
                }
            }
        }.start();
    }

    /**
     * Add a review to the review counter
     *
     * @param done True if a review is done, otherwise false
     */
    private void addReview(final boolean done) {
        if (done) {
            settingsContainer.setReviewTimes(3);
        } else {
            settingsContainer.setReviewTimes(settingsContainer.getReviewTimes() + 1);
        }

        settingsContainer.saveSettings(getApplicationContext());
    }

    @Override
    protected void onResume() {
        paused = false;

        settingsContainer.loadSettings(getApplicationContext());

        final String selectedLanguage = settingsContainer.getLanguageCode();
        if (!lastLanguage.equals(selectedLanguage)) {
            LocaleHelper.setLocale(getApplicationContext(), selectedLanguage);
            recreate();
        }

        loadTheme();

        super.onResume();
    }

    @Override
    protected void onPause() {
        paused = true;
        super.onPause();
    }

    /**
     * Load the content and logic for the file hashing view
     *
     * @param savedInstance The Bundle that contains saved information
     */
    private void loadFileHashContent(final Bundle savedInstance) {
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
        }

        pgbFile = findViewById(R.id.PgbFile);
        mRecyclerViewFile = findViewById(R.id.file_recycler);
        mRecyclerViewFile.setHasFixedSize(true);
        mLayoutManagerFile = new LinearLayoutManager(this);
        mRecyclerViewFile.setLayoutManager(mLayoutManagerFile);

        final ImageButton btnOpenFile = findViewById(R.id.ImgBtnFileData);
        edtFilePath = findViewById(R.id.EdtFile_name);
        final Button btnGenerate = findViewById(R.id.ButtonGenerateFile);
        edtFileCompare = findViewById(R.id.Edit_FileCompare);

        if (savedInstance != null) {
            if (savedInstance.containsKey("FILE_PATH")) {
                edtFilePath.setText(savedInstance.getString("FILE_PATH"));
            }
            if (savedInstance.containsKey("FILE_COMPARE")) {
                edtFileCompare.setText(savedInstance.getString("FILE_COMPARE"));
            }
            if (savedInstance.containsKey("FILE_KEY")) {
                fileDataArrayList = savedInstance.getParcelableArrayList("FILE_KEY");
                if (fileDataArrayList != null) {
                    mAdapterFile = new DataAdapter(fileDataArrayList);
                    mAdapterFile.notifyDataSetChanged();
                }
            }
        }

        mRecyclerViewFile.setAdapter(mAdapterFile);

        btnOpenFile.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
            } else {
                final Intent intent = new Intent()
                        .setType("*/*")
                        .setAction(Intent.ACTION_GET_CONTENT)
                        .addCategory(Intent.CATEGORY_OPENABLE);

                activityResultLauncher.launch(Intent.createChooser(intent, getString(R.string.dialog_select_file)));
            }
        });

        btnGenerate.setOnClickListener(v -> {
            if (fileLoading) return;
            if (!new File(getBaseContext().getCacheDir(), tmpFile).exists()) {
                Toast.makeText(getApplicationContext(), R.string.error_no_file, Toast.LENGTH_LONG).show();
                return;
            }

            mRecyclerViewFile.setAdapter(null);

            fileDataArrayList = new ArrayList<>();
            mAdapterFile = new DataAdapter(fileDataArrayList);

            mRecyclerViewFile.setAdapter(mAdapterFile);

            fileDataArrayList.clear();
            mAdapterFile.notifyDataSetChanged();

            String compare = "";
            if (edtFileCompare.getText() != null) {
                compare = edtFileCompare.getText().toString();
            }

            try {
                final HashGenerator fileHashGenerator = new HashGenerator(new File(getApplicationContext().getCacheDir(), tmpFile), getHashAlgorithms(), compare);
                fileLoading = true;

                CompletableFuture.supplyAsync(fileHashGenerator::generateHashes)
                        .thenAccept(s -> runOnUiThread(() -> {
                            fileLoading = false;
                            pgbFile.setVisibility(View.GONE);

                            for (final HashData d : s) {
                                fileDataArrayList.add(d);
                                mAdapterFile.notifyItemInserted(fileDataArrayList.size());
                            }
                        }));

                pgbFile.setVisibility(View.VISIBLE);
            } catch (final IOException e) {
                Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                pgbFile.setVisibility(View.GONE);
            }
        });
    }

    /**
     * Load the content and logic for the text hashing view
     *
     * @param savedInstance The Bundle that contains saved information
     */
    private void loadTextHashContent(final Bundle savedInstance) {
        pgbText = findViewById(R.id.PgbText);
        mRecyclerViewText = findViewById(R.id.text_recycler);
        mRecyclerViewText.setHasFixedSize(true);
        mLayoutManagerFile = new LinearLayoutManager(this);
        mRecyclerViewText.setLayoutManager(mLayoutManagerFile);

        edtTextData = findViewById(R.id.EdtText_Content);
        final Button btnGenerate = findViewById(R.id.ButtonGenerateText);
        edtTextCompare = findViewById(R.id.Edit_TextCompare);

        if (savedInstance != null) {
            if (savedInstance.containsKey("TEXT_DATA")) {
                edtTextData.setText(savedInstance.getString("TEXT_DATA"));
            }
            if (savedInstance.containsKey("TEXT_COMPARE")) {
                edtTextCompare.setText(savedInstance.getString("TEXT_COMPARE"));
            }
            if (savedInstance.containsKey("TEXT_KEY")) {
                textDataArrayList = savedInstance.getParcelableArrayList("TEXT_KEY");
                if (textDataArrayList != null) {
                    mAdapterText = new DataAdapter(textDataArrayList);
                    mAdapterText.notifyDataSetChanged();
                }
            }
        }

        mRecyclerViewText.setAdapter(mAdapterText);

        btnGenerate.setOnClickListener(v -> {
            if (textLoading) return;
            mRecyclerViewText.setAdapter(null);

            textDataArrayList = new ArrayList<>();
            mAdapterText = new DataAdapter(textDataArrayList);

            mRecyclerViewText.setAdapter(mAdapterText);

            fileDataArrayList.clear();
            mAdapterText.notifyDataSetChanged();

            if (edtTextData.getText() == null || edtTextData.getText().toString().length() == 0) {
                Toast.makeText(MainActivity.this, R.string.toast_error_notext, Toast.LENGTH_SHORT).show();
                return;
            }

            final String data = edtTextData.getText().toString();
            String compare = "";
            if (edtTextCompare.getText() != null) {
                compare = edtTextCompare.getText().toString();
            }


            final HashGenerator textHashGenerator = new HashGenerator(data.getBytes(), getHashAlgorithms(), compare);
            textLoading = true;

            CompletableFuture.supplyAsync(textHashGenerator::generateHashes)
                    .thenAccept(s -> runOnUiThread(() -> {
                        textLoading = false;
                        pgbText.setVisibility(View.GONE);

                        for (final HashData d : s) {
                            textDataArrayList.add(d);
                            mAdapterText.notifyItemInserted(textDataArrayList.size());
                        }
                    }));

            pgbText.setVisibility(View.VISIBLE);
        });
    }

    /**
     * Get the List of HashAlgorithm enums that can be used, according to user preferences
     *
     * @return The List of HashingAlgorithm enums that can be used, according to user preferences
     */
    private List<HashAlgorithm> getHashAlgorithms() {
        final List<HashAlgorithm> hashAlgorithms = new ArrayList<>();
        if (settingsContainer.isCalculateMd5())
            hashAlgorithms.add(HashAlgorithm.md5);
        if (settingsContainer.isCalculateSha1())
            hashAlgorithms.add(HashAlgorithm.sha1);
        if (settingsContainer.isCalculateSha224())
            hashAlgorithms.add(HashAlgorithm.sha224);
        if (settingsContainer.isCalculateSha256())
            hashAlgorithms.add(HashAlgorithm.sha256);
        if (settingsContainer.isCalculateSha384())
            hashAlgorithms.add(HashAlgorithm.sha384);
        if (settingsContainer.isCalculateSha512())
            hashAlgorithms.add(HashAlgorithm.sha512);
        if (settingsContainer.isCalculateCrc32())
            hashAlgorithms.add(HashAlgorithm.crc32);
        return hashAlgorithms;
    }

    /**
     * Load the content and logic for the help view
     */
    private void loadHelpContent() {
        final Button btnWebsite = findViewById(R.id.ButtonWebsite);
        final Button btnSupport = findViewById(R.id.ButtonSupport);

        btnWebsite.setOnClickListener(v -> IntentUtils.openSite(v.getContext(), "http://codedead.com/"));

        btnSupport.setOnClickListener(v -> new ShareCompat.IntentBuilder(MainActivity.this)
                .setType("message/rfc822")
                .addEmailTo("admin@codedead.com")
                .setSubject("DeadHash - Android")
                .setText("")
                .setChooserTitle(R.string.text_send_mail)
                .startChooser());
    }

    /**
     * Load the content and logic for the about view
     */
    private void loadAboutContent() {
        final ImageButton btnFacebook = findViewById(R.id.BtnFacebook);
        final ImageButton btnTwitter = findViewById(R.id.BtnTwitter);
        final ImageButton btnWebsite = findViewById(R.id.BtnWebsiteAbout);
        final TextView txtAbout = findViewById(R.id.TxtAbout);

        btnWebsite.setOnClickListener(v -> IntentUtils.openSite(v.getContext(), "http://codedead.com/"));
        btnFacebook.setOnClickListener(v -> IntentUtils.openSite(v.getContext(), "https://facebook.com/deadlinecodedead"));
        btnTwitter.setOnClickListener(v -> IntentUtils.openSite(v.getContext(), "https://twitter.com/C0DEDEAD"));
        txtAbout.setMovementMethod(LinkMovementMethod.getInstance());
    }

    @Override
    public void onSaveInstanceState(final Bundle savedInstanceState) {
        savedInstanceState.putInt("TAB_NUMBER", viewFlipper.getDisplayedChild());
        savedInstanceState.putString("FILE_PATH", edtFilePath.getText().toString());
        savedInstanceState.putString("FILE_COMPARE", edtFileCompare.getText().toString());
        savedInstanceState.putParcelableArrayList("FILE_KEY", fileDataArrayList);
        savedInstanceState.putString("TEXT_DATA", edtTextData.getText().toString());
        savedInstanceState.putString("TEXT_COMPARE", edtTextCompare.getText().toString());
        savedInstanceState.putParcelableArrayList("TEXT_KEY", textDataArrayList);
        savedInstanceState.putBoolean("KEEP_FILE", true);
        super.onSaveInstanceState(savedInstanceState);
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

    @Override
    public void onBackPressed() {
        final DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (doubleBackToExitPressedOnce) {
                super.onBackPressed();
                return;
            }

            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, R.string.toast_back_again, Toast.LENGTH_SHORT).show();

            new Handler(Looper.getMainLooper()).postDelayed(() -> doubleBackToExitPressedOnce = false, 2000);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull final MenuItem item) {
        int page = 0;

        final int itemId = item.getItemId();
        if (itemId == R.id.nav_text) {
            page = 1;
        } else if (itemId == R.id.nav_help) {
            page = 2;
        } else if (itemId == R.id.nav_about) {
            page = 3;
        }

        viewFlipper.setDisplayedChild(page);

        final DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
