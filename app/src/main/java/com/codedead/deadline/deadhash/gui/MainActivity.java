package com.codedead.deadline.deadhash.gui;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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

import com.codedead.deadline.deadhash.R;
import com.codedead.deadline.deadhash.domain.DataAdapter;
import com.codedead.deadline.deadhash.domain.EncryptionData;
import com.codedead.deadline.deadhash.domain.FileDialog;
import com.codedead.deadline.deadhash.domain.FileHashGenerator;
import com.codedead.deadline.deadhash.domain.HashGenerator;
import com.codedead.deadline.deadhash.domain.HashResponse;
import com.codedead.deadline.deadhash.domain.LocaleHelper;
import com.codedead.deadline.deadhash.domain.TextHashGenerator;
import com.tapadoo.alerter.Alerter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, HashResponse {
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

    private ArrayList<EncryptionData> fileDataArrayList = new ArrayList<>();
    private ArrayList<EncryptionData> textDataArrayList = new ArrayList<>();

    private DataAdapter mAdapterFile = new DataAdapter(fileDataArrayList);
    private DataAdapter mAdapterText = new DataAdapter(textDataArrayList);

    private SharedPreferences sharedPreferences;

    private boolean fileLoading;
    private boolean textLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        sharedPreferences = getApplicationContext().getSharedPreferences(getString(R.string.preferences_file_key), Context.MODE_PRIVATE);
        LocaleHelper.setLocale(this, sharedPreferences.getString("language", "en"));

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        viewFlipper = (ViewFlipper) findViewById(R.id.vf);

        if (savedInstanceState != null) {
            int flipperPosition = savedInstanceState.getInt("TAB_NUMBER");
            viewFlipper.setDisplayedChild(flipperPosition);

            if (flipperPosition > 1) {
                navigationView.setCheckedItem(navigationView.getMenu().getItem(1).getSubMenu().getItem(flipperPosition - 2).getItemId());
            } else {
                navigationView.setCheckedItem(navigationView.getMenu().getItem(0).getSubMenu().getItem(flipperPosition).getItemId());
            }
        } else {
            navigationView.setCheckedItem(navigationView.getMenu().getItem(0).getSubMenu().getItem(0).getItemId());
        }

        content_file(savedInstanceState);
        content_text(savedInstanceState);
        content_help();
        content_about();
        content_settings();

        content_alerts();
    }

    private void content_alerts() {
        if (sharedPreferences.getInt("reviewTimes", 0) >= 2) return;

        Random rnd = new Random();

        new CountDownTimer(rnd.nextInt(30) * 1000, 1000) {

            @Override
            public void onTick(long millisUntilFinished) {
                //Not used
            }

            @Override
            public void onFinish() {
                Alerter.create(MainActivity.this)
                        .setTitle(R.string.alert_review_title)
                        .setText(R.string.alert_review_text)
                        .setIcon(R.drawable.ic_rate_review)
                        .setBackgroundColor(R.color.colorAccent)
                        .setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                addReview(true);
                                openPlayStore();
                            }
                        })
                        .show();
                addReview(false);
            }
        }.start();
    }

    private void addReview(boolean done) {
        SharedPreferences.Editor editor = sharedPreferences.edit();

        if (done) {
            editor.putInt("reviewTimes", 3);
        } else {
            editor.putInt("reviewTimes", sharedPreferences.getInt("reviewTimes", 0) + 1);
        }

        editor.apply();
    }

    private void content_file(Bundle savedInstance) {
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
        }

        pgbFile = (ProgressBar) findViewById(R.id.PgbFile);
        mRecyclerViewFile = (RecyclerView) findViewById(R.id.file_recycler);
        mRecyclerViewFile.setHasFixedSize(true);
        mLayoutManagerFile = new LinearLayoutManager(this);
        mRecyclerViewFile.setLayoutManager(mLayoutManagerFile);

        ImageButton btnOpenFile = (ImageButton) findViewById(R.id.ImgBtnFileData);
        edtFilePath = (EditText) findViewById(R.id.EdtFile_name);
        Button btnGenerate = (Button) findViewById(R.id.ButtonGenerateFile);
        edtFileCompare = (EditText) findViewById(R.id.Edit_FileCompare);

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
                    File mPath = Environment.getExternalStorageDirectory();
                    FileDialog fileDialog = new FileDialog(MainActivity.this, mPath, null);
                    fileDialog.addFileListener(new FileDialog.FileSelectedListener() {
                        public void fileSelected(File file) {
                            edtFilePath.setText(file.toString());
                        }
                    });
                    fileDialog.showDialog();
                }
            }
        });


        btnGenerate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fileLoading) return;

                mRecyclerViewFile.setAdapter(null);

                fileDataArrayList = new ArrayList<>();
                mAdapterFile = new DataAdapter(fileDataArrayList);

                mRecyclerViewFile.setAdapter(mAdapterFile);

                fileDataArrayList.clear();
                mAdapterFile.notifyDataSetChanged();

                File file = new File(edtFilePath.getText().toString());
                if (!file.exists()) {
                    Toast.makeText(MainActivity.this, R.string.toast_file_not_found, Toast.LENGTH_SHORT).show();
                    return;
                }

                String compare = "";
                if (edtFileCompare.getText() != null) {
                    compare = edtFileCompare.getText().toString();
                }

                try {
                    HashGenerator fileHashGenerator = new FileHashGenerator(
                            file,
                            sharedPreferences.getBoolean("md5", true),
                            sharedPreferences.getBoolean("sha1", true),
                            sharedPreferences.getBoolean("sha224", true),
                            sharedPreferences.getBoolean("sha256", true),
                            sharedPreferences.getBoolean("sha384", true),
                            sharedPreferences.getBoolean("sha512", true),
                            sharedPreferences.getBoolean("crc32", true),
                            compare);
                    fileLoading = true;
                    fileHashGenerator.delegate = MainActivity.this;
                    fileHashGenerator.execute();

                    pgbFile.setVisibility(View.VISIBLE);
                } catch (IOException e) {
                    Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    pgbFile.setVisibility(View.GONE);
                }
            }
        });
    }

    private void content_text(Bundle savedInstance) {
        pgbText = (ProgressBar) findViewById(R.id.PgbText);
        mRecyclerViewText = (RecyclerView) findViewById(R.id.text_recycler);
        mRecyclerViewText.setHasFixedSize(true);
        mLayoutManagerFile = new LinearLayoutManager(this);
        mRecyclerViewText.setLayoutManager(mLayoutManagerFile);

        edtTextData = (EditText) findViewById(R.id.EdtText_Content);
        Button btnGenerate = (Button) findViewById(R.id.ButtonGenerateText);
        edtTextCompare = (EditText) findViewById(R.id.Edit_TextCompare);

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

                String data = edtTextData.getText().toString();
                String compare = "";
                if (edtTextCompare.getText() != null) {
                    compare = edtTextCompare.getText().toString();
                }


                HashGenerator textHashGenerator = new TextHashGenerator(
                        data.getBytes(),
                        sharedPreferences.getBoolean("md5", true),
                        sharedPreferences.getBoolean("sha1", true),
                        sharedPreferences.getBoolean("sha224", true),
                        sharedPreferences.getBoolean("sha256", true),
                        sharedPreferences.getBoolean("sha384", true),
                        sharedPreferences.getBoolean("sha512", true),
                        sharedPreferences.getBoolean("crc32", true),
                        compare);
                textLoading = true;
                textHashGenerator.delegate = MainActivity.this;
                textHashGenerator.execute();
                pgbText.setVisibility(View.VISIBLE);
            }
        });
    }

    private void content_help() {
        Button btnWebsite = (Button) findViewById(R.id.ButtonWebsite);
        Button btnSupport = (Button) findViewById(R.id.ButtonSupport);

        btnWebsite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSite("http://codedead.com/");
            }
        });

        btnSupport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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

    private void content_about() {
        ImageButton btnFacebook = (ImageButton) findViewById(R.id.BtnFacebook);
        ImageButton btnTwitter = (ImageButton) findViewById(R.id.BtnTwitter);
        Button btnWebsite = (Button) findViewById(R.id.BtnWebsiteAbout);

        btnWebsite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSite("http://codedead.com/");
            }
        });

        btnFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSite("https://facebook.com/deadlinecodedead");
            }
        });

        btnTwitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSite("https://twitter.com/C0DEDEAD");
            }
        });
    }

    private void content_settings() {
        final Spinner spnLanguages = (Spinner) findViewById(R.id.SpnLanguages);
        final CheckBox ChbMD5 = (CheckBox) findViewById(R.id.ChbMD5);
        final CheckBox ChbSHA1 = (CheckBox) findViewById(R.id.ChbSHA1);
        final CheckBox ChbSHA224 = (CheckBox) findViewById(R.id.ChbSHA224);
        final CheckBox ChbSHA256 = (CheckBox) findViewById(R.id.ChbSHA256);
        final CheckBox ChbSHA384 = (CheckBox) findViewById(R.id.ChbSHA384);
        final CheckBox ChbSHA512 = (CheckBox) findViewById(R.id.ChbSHA512);
        final CheckBox ChbCRC32 = (CheckBox) findViewById(R.id.ChbCRC32);

        Button btnReset = (Button) findViewById(R.id.BtnResetSettings);
        Button btnSave = (Button) findViewById(R.id.BtnSaveSettings);

        String l = sharedPreferences.getString("language", "en");

        switch (l) {
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
        }

        ChbMD5.setChecked(sharedPreferences.getBoolean("md5", true));
        ChbSHA1.setChecked(sharedPreferences.getBoolean("sha1", true));
        ChbSHA224.setChecked(sharedPreferences.getBoolean("sha224", true));
        ChbSHA256.setChecked(sharedPreferences.getBoolean("sha256", true));
        ChbSHA384.setChecked(sharedPreferences.getBoolean("sha384", true));
        ChbSHA512.setChecked(sharedPreferences.getBoolean("sha512", true));
        ChbCRC32.setChecked(sharedPreferences.getBoolean("crc32", true));

        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveSettings("en",true, true, true, true, true, true, true);
                Context c = LocaleHelper.setLocale(getApplicationContext(), sharedPreferences.getString("language", "en"));
                Toast.makeText(MainActivity.this, c.getString(R.string.toast_settings_reset), Toast.LENGTH_SHORT).show();
                recreate();
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
                }

                saveSettings(lang, ChbMD5.isChecked(), ChbSHA1.isChecked(), ChbSHA224.isChecked(), ChbSHA256.isChecked(), ChbSHA384.isChecked(), ChbSHA512.isChecked(), ChbCRC32.isChecked());
                Context c = LocaleHelper.setLocale(getApplicationContext(), sharedPreferences.getString("language", "en"));
                Toast.makeText(MainActivity.this, c.getString(R.string.toast_settings_save), Toast.LENGTH_SHORT).show();
                recreate();
            }
        });
    }

    private void saveSettings(String lang, boolean MD5, boolean SHA1, boolean SHA224, boolean SHA256, boolean SHA384, boolean SHA512, boolean CRC32) {
        SharedPreferences.Editor edit = sharedPreferences.edit();

        edit.putString("language", lang);
        edit.putBoolean("md5", MD5);
        edit.putBoolean("sha1", SHA1);
        edit.putBoolean("sha224", SHA224);
        edit.putBoolean("sha256", SHA256);
        edit.putBoolean("sha384", SHA384);
        edit.putBoolean("sha512", SHA512);
        edit.putBoolean("crc32", CRC32);

        edit.apply();
    }

    private void openSite(String site) {
        try {
            Uri uriUrl = Uri.parse(site);
            Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
            startActivity(launchBrowser);
        } catch (Exception ignored) {

        }
    }

    private void openPlayStore() {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("market://details?id=com.codedead.deadline.deadhash"));
            startActivity(intent);
        } catch (Exception ignored) {

        }
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
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        LocaleHelper.onAttach(getBaseContext());
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.onAttach(base));
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
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
        int id = item.getItemId();
        int page = 0;

        if (id == R.id.nav_text) {
            page = 1;
        } else if (id == R.id.nav_help) {
            page = 2;
        } else if (id == R.id.nav_about) {
            page = 3;
        } else if (id == R.id.nav_manage) {
            page = 4;
        }

        viewFlipper.setDisplayedChild(page);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void hashDataFile(List<EncryptionData> data) {
        fileLoading = false;
        pgbFile.setVisibility(View.GONE);

        for (EncryptionData d : data) {
            fileDataArrayList.add(d);
            mAdapterFile.notifyItemInserted(fileDataArrayList.size());
        }
    }

    @Override
    public void hashDataText(List<EncryptionData> data) {
        textLoading = false;
        pgbText.setVisibility(View.GONE);

        for (EncryptionData d : data) {
            textDataArrayList.add(d);
            mAdapterText.notifyItemInserted(textDataArrayList.size());
        }
    }
}
