package com.codedead.deadhash.gui;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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

import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.codedead.deadhash.R;
import com.codedead.deadhash.domain.utils.DataAdapter;
import com.codedead.deadhash.domain.objects.hashgenerator.HashData;
import com.codedead.deadhash.domain.objects.hashgenerator.FileHashGenerator;
import com.codedead.deadhash.domain.objects.hashgenerator.HashGenerator;
import com.codedead.deadhash.domain.interfaces.hashgenerator.IHashResponse;
import com.codedead.deadhash.domain.utils.LocaleHelper;
import com.codedead.deadhash.domain.objects.hashgenerator.TextHashGenerator;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, IHashResponse {
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

    private Spinner spnLanguages;
    private CheckBox ChbMD5;
    private CheckBox ChbSHA1;
    private CheckBox ChbSHA224;
    private CheckBox ChbSHA256;
    private CheckBox ChbSHA384;
    private CheckBox ChbSHA512;
    private CheckBox ChbCRC32;

    private final String tmpFile = "tmpFile";

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        settingsContainer.loadSettings(getApplicationContext());
        LocaleHelper.setLocale(this, settingsContainer.getLanguageCode());

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
        } else {
            navigationView.setCheckedItem(navigationView.getMenu().getItem(0).getSubMenu().getItem(0).getItemId());
        }

        spnLanguages = findViewById(R.id.SpnLanguages);
        ChbMD5 = findViewById(R.id.ChbMD5);
        ChbSHA1 = findViewById(R.id.ChbSHA1);
        ChbSHA224 = findViewById(R.id.ChbSHA224);
        ChbSHA256 = findViewById(R.id.ChbSHA256);
        ChbSHA384 = findViewById(R.id.ChbSHA384);
        ChbSHA512 = findViewById(R.id.ChbSHA512);
        ChbCRC32 = findViewById(R.id.ChbCRC32);

        loadFileHashContent(savedInstanceState);
        loadTextHashContent(savedInstanceState);
        loadHelpContent();
        loadAboutContent();
        loadSettingsContent();

        loadAlertContent();

        // Cleanup of previous runs, if applicable
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

                reviewBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();

                        addReview(true);
                        IntentUtils.openPlayStore(MainActivity.this.getApplicationContext());
                    }
                });

                reviewBuilder.setNegativeButton(R.string.alert_review_never, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        addReview(true);
                    }
                });

                reviewBuilder.setNeutralButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        addReview(false);
                    }
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

        btnOpenFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
                } else {
                    final Intent intent = new Intent()
                            .setType("*/*")
                            .setAction(Intent.ACTION_GET_CONTENT)
                            .addCategory(Intent.CATEGORY_OPENABLE);

                    startActivityForResult(Intent.createChooser(intent, getString(R.string.dialog_select_file)), 123);
                }
            }
        });

        btnGenerate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fileLoading) return;
                if (!new File(getApplicationContext().getCacheDir(), tmpFile).exists()) return;

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
                    final HashGenerator fileHashGenerator = new FileHashGenerator(new File(getApplicationContext().getCacheDir(), tmpFile), getHashAlgorithms(), compare);
                    fileLoading = true;
                    fileHashGenerator.hashResponse = MainActivity.this;
                    fileHashGenerator.execute();

                    pgbFile.setVisibility(View.VISIBLE);
                } catch (IOException e) {
                    Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    pgbFile.setVisibility(View.GONE);
                }
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

        btnGenerate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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


                final HashGenerator textHashGenerator = new TextHashGenerator(data.getBytes(), getHashAlgorithms(), compare);
                textLoading = true;
                textHashGenerator.hashResponse = MainActivity.this;
                textHashGenerator.execute();
                pgbText.setVisibility(View.VISIBLE);
            }
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

        btnWebsite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                IntentUtils.openSite(v.getContext(), "http://codedead.com/");
            }
        });

        btnSupport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                ShareCompat.IntentBuilder.from(MainActivity.this)
                        .setType("message/rfc822")
                        .addEmailTo("admin@codedead.com")
                        .setSubject("DeadHash - Android")
                        .setText("")
                        .setChooserTitle(R.string.text_send_mail)
                        .startChooser();
            }
        });
    }

    /**
     * Load the content and logic for the about view
     */
    private void loadAboutContent() {
        final ImageButton btnFacebook = findViewById(R.id.BtnFacebook);
        final ImageButton btnTwitter = findViewById(R.id.BtnTwitter);
        final ImageButton btnWebsite = findViewById(R.id.BtnWebsiteAbout);

        btnWebsite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                IntentUtils.openSite(v.getContext(), "http://codedead.com/");
            }
        });

        btnFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                IntentUtils.openSite(v.getContext(), "https://facebook.com/deadlinecodedead");
            }
        });

        btnTwitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                IntentUtils.openSite(v.getContext(), "https://twitter.com/C0DEDEAD");
            }
        });
    }

    /**
     * Load the settings into the view
     */
    private void loadSettings() {
        switch (settingsContainer.getLanguageCode()) {
            default:
            case "en":
                spnLanguages.setSelection(0);
                break;
            case "nl":
                spnLanguages.setSelection(1);
                break;
            case "fr":
                spnLanguages.setSelection(2);
                break;
            case "de":
                spnLanguages.setSelection(3);
                break;
            case "it":
                spnLanguages.setSelection(4);
                break;
            case "pt":
                spnLanguages.setSelection(5);
        }

        ChbMD5.setChecked(settingsContainer.isCalculateMd5());
        ChbSHA1.setChecked(settingsContainer.isCalculateSha1());
        ChbSHA224.setChecked(settingsContainer.isCalculateSha224());
        ChbSHA256.setChecked(settingsContainer.isCalculateSha256());
        ChbSHA384.setChecked(settingsContainer.isCalculateSha384());
        ChbSHA512.setChecked(settingsContainer.isCalculateSha512());
        ChbCRC32.setChecked(settingsContainer.isCalculateCrc32());
    }

    /**
     * Load the content and logic for the settings view
     */
    private void loadSettingsContent() {
        final Button btnReset = findViewById(R.id.BtnResetSettings);
        final Button btnSave = findViewById(R.id.BtnSaveSettings);
        loadSettings();

        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveSettings("en", true, true, true, true, true, true, true);
                final Context c = LocaleHelper.setLocale(getApplicationContext(), settingsContainer.getLanguageCode());
                Toast.makeText(MainActivity.this, c.getString(R.string.toast_settings_reset), Toast.LENGTH_SHORT).show();
                recreate();
                loadSettings();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String lang;
                switch (spnLanguages.getSelectedItemPosition()) {
                    default:
                        lang = "en";
                        break;
                    case 1:
                        lang = "nl";
                        break;
                    case 2:
                        lang = "fr";
                        break;
                    case 3:
                        lang = "de";
                        break;
                    case 4:
                        lang = "it";
                        break;
                    case 5:
                        lang = "pt";
                }

                saveSettings(lang, ChbMD5.isChecked(), ChbSHA1.isChecked(), ChbSHA224.isChecked(), ChbSHA256.isChecked(), ChbSHA384.isChecked(), ChbSHA512.isChecked(), ChbCRC32.isChecked());
                final Context c = LocaleHelper.setLocale(getApplicationContext(), settingsContainer.getLanguageCode());
                Toast.makeText(MainActivity.this, c.getString(R.string.toast_settings_save), Toast.LENGTH_SHORT).show();
                recreate();
                loadSettings();
            }
        });
    }

    /**
     * Save the user preferences
     *
     * @param lang   The language code
     * @param md5    Whether or not MD5 hashes should be calculated
     * @param sha1   Whether or not SHA1 hashes should be calculated
     * @param sha224 Whether or not SHA224 hashes should be calculated
     * @param sha256 Whether or not SHA256 hashes should be calculated
     * @param sha384 Whether or not SHA384 hashes should be calculated
     * @param sha512 Whether or not SHA512 hashes should be calculated
     * @param crc32  Whether or not CRC32 values should be calculated
     */
    private void saveSettings(final String lang, final boolean md5, final boolean sha1, final boolean sha224, final boolean sha256, final boolean sha384, final boolean sha512, final boolean crc32) {
        settingsContainer.setLanguageCode(lang);
        settingsContainer.setCalculateMd5(md5);
        settingsContainer.setCalculateSha1(sha1);
        settingsContainer.setCalculateSha224(sha224);
        settingsContainer.setCalculateSha256(sha256);
        settingsContainer.setCalculateSha384(sha384);
        settingsContainer.setCalculateSha512(sha512);
        settingsContainer.setCalculateCrc32(crc32);

        settingsContainer.saveSettings(getApplicationContext());
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putInt("TAB_NUMBER", viewFlipper.getDisplayedChild());
        savedInstanceState.putString("FILE_PATH", edtFilePath.getText().toString());
        savedInstanceState.putString("FILE_COMPARE", edtFileCompare.getText().toString());
        savedInstanceState.putParcelableArrayList("FILE_KEY", fileDataArrayList);
        savedInstanceState.putString("TEXT_DATA", edtTextData.getText().toString());
        savedInstanceState.putString("TEXT_COMPARE", edtTextCompare.getText().toString());
        savedInstanceState.putParcelableArrayList("TEXT_KEY", textDataArrayList);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        LocaleHelper.onAttach(getBaseContext());
    }

    @Override
    protected void attachBaseContext(Context base) {
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

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, 2000);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int page = 0;

        switch (item.getItemId()) {
            case R.id.nav_text:
                page = 1;
                break;
            case R.id.nav_help:
                page = 2;
                break;
            case R.id.nav_about:
                page = 3;
                break;
            case R.id.nav_manage:
                page = 4;
                break;
        }

        viewFlipper.setDisplayedChild(page);

        final DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void hashDataFile(List<HashData> data) {
        fileLoading = false;
        pgbFile.setVisibility(View.GONE);

        for (final HashData d : data) {
            fileDataArrayList.add(d);
            mAdapterFile.notifyItemInserted(fileDataArrayList.size());
        }
    }

    @Override
    public void hashDataText(final List<HashData> data) {
        textLoading = false;
        pgbText.setVisibility(View.GONE);

        for (final HashData d : data) {
            textDataArrayList.add(d);
            mAdapterText.notifyItemInserted(textDataArrayList.size());
        }
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, @Nullable final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 123 && resultCode == RESULT_OK) {
            if (data != null) {
                final Uri selectedFileUri = data.getData();
                if (selectedFileUri != null) {
                    try (final InputStream selectedFileStream = getContentResolver().openInputStream(selectedFileUri)) {
                        final File outputFile = new File(getApplicationContext().getCacheDir(), tmpFile);

                        try (final FileOutputStream outputStream = new FileOutputStream(outputFile)) {
                            if (selectedFileStream != null) {
                                StreamUtility.copyStream(selectedFileStream, outputStream);
                                edtFilePath.setText(selectedFileUri.getPath());
                            } else {
                                Toast.makeText(this, R.string.error_open_file, Toast.LENGTH_SHORT).show();
                            }
                        } catch (IOException ex) {
                            Toast.makeText(this, R.string.error_copy_file, Toast.LENGTH_SHORT).show();
                        }
                    } catch (IOException ex) {
                        Toast.makeText(this, R.string.error_open_file, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this, R.string.error_open_file, Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
