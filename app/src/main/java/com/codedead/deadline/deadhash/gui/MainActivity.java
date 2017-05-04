package com.codedead.deadline.deadhash.gui;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.codedead.deadline.deadhash.R;
import com.codedead.deadline.deadhash.domain.DataAdapter;
import com.codedead.deadline.deadhash.domain.EncryptionData;
import com.codedead.deadline.deadhash.domain.FileDialog;
import com.codedead.deadline.deadhash.domain.HashService;
import com.codedead.deadline.deadhash.domain.LocaleHelper;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private boolean doubleBackToExitPressedOnce;

    private ViewFlipper viewFlipper;

    private RecyclerView.LayoutManager mLayoutManagerFile;

    private DataAdapter mAdapterFile;
    private DataAdapter mAdapterText;

    private ArrayList<EncryptionData> fileDataArrayList;
    private ArrayList<EncryptionData> textDataArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        fileDataArrayList = new ArrayList<>();
        textDataArrayList = new ArrayList<>();

        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(getString(R.string.preferences_file_key), Context.MODE_PRIVATE);

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
        navigationView.getMenu().getItem(0).getSubMenu().getItem(0).setChecked(true);

        viewFlipper = (ViewFlipper) findViewById(R.id.vf);

        if (savedInstanceState != null) {
            int flipperPosition = savedInstanceState.getInt("TAB_NUMBER");
            viewFlipper.setDisplayedChild(flipperPosition);

            if (flipperPosition > 1) {

                navigationView.getMenu().getItem(1).getSubMenu().getItem(flipperPosition - 2).setChecked(true);
            } else {
                navigationView.getMenu().getItem(0).getSubMenu().getItem(flipperPosition).setChecked(true);
            }
        }

        content_file();
        content_text();
        content_help();
        content_about();
    }

    private void content_file() {
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
        }

        RecyclerView mRecyclerViewFile = (RecyclerView) findViewById(R.id.file_recycler);
        mRecyclerViewFile.setHasFixedSize(true);
        mLayoutManagerFile = new LinearLayoutManager(this);
        mRecyclerViewFile.setLayoutManager(mLayoutManagerFile);

        mAdapterFile = new DataAdapter(fileDataArrayList);
        mRecyclerViewFile.setAdapter(mAdapterFile);

        ImageButton btnOpenFile = (ImageButton) findViewById(R.id.ImgBtnFileData);
        final EditText edtPath = (EditText) findViewById(R.id.EdtFile_name);
        Button btnGenerate = (Button) findViewById(R.id.ButtonGenerateFile);

        final EditText edtCompare = (EditText) findViewById(R.id.Edit_FileCompare);

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
                            edtPath.setText(file.toString());
                        }
                    });
                    fileDialog.showDialog();
                }
            }
        });


        btnGenerate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int size = fileDataArrayList.size();
                fileDataArrayList.clear();
                mAdapterFile.notifyItemRangeRemoved(0, size);

                File file = new File(edtPath.getText().toString());
                if (!file.exists()) {
                    Toast.makeText(MainActivity.this, R.string.toast_file_not_found, Toast.LENGTH_SHORT).show();
                    return;
                }

                String compare = "";
                if (edtCompare.getText() != null) {
                    compare = edtCompare.getText().toString();
                }

                String md5 = HashService.calculateFileHash(file, "MD5");
                String sha1 = HashService.calculateFileHash(file, "SHA-1");
                String sha256 = HashService.calculateFileHash(file, "SHA-256");
                String sha384 = HashService.calculateFileHash(file, "SHA-384");
                String sha512 = HashService.calculateFileHash(file, "SHA-512");

                String crc32 = HashService.calculateFileCRC32(file);

                addFileHash("MD5", md5, compare);
                addFileHash("SHA-1", sha1, compare);
                addFileHash("SHA-256", sha256, compare);
                addFileHash("SHA-384", sha384, compare);
                addFileHash("SHA-512", sha512, compare);

                addFileHash("CRC32", crc32, compare);
            }
        });
    }

    private void content_text() {
        RecyclerView mRecyclerViewText = (RecyclerView) findViewById(R.id.text_recycler);
        mRecyclerViewText.setHasFixedSize(true);
        mLayoutManagerFile = new LinearLayoutManager(this);
        mRecyclerViewText.setLayoutManager(mLayoutManagerFile);

        mAdapterText = new DataAdapter(textDataArrayList);
        mRecyclerViewText.setAdapter(mAdapterText);

        final EditText edtData = (EditText) findViewById(R.id.EdtText_Content);
        Button btnGenerate = (Button) findViewById(R.id.ButtonGenerateText);

        final EditText edtCompare = (EditText) findViewById(R.id.Edit_TextCompare);

        btnGenerate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edtData.getText() == null || edtData.getText().toString().length() == 0) {
                    Toast.makeText(MainActivity.this, R.string.toast_error_notext, Toast.LENGTH_SHORT).show();
                    return;
                }

                int size = textDataArrayList.size();
                textDataArrayList.clear();
                mAdapterText.notifyItemRangeRemoved(0, size);

                String data = edtData.getText().toString();
                String compare = "";
                if (edtCompare.getText() != null) {
                    compare = edtCompare.getText().toString();
                }

                String md5 = HashService.calculateStringHash(data, "MD5");
                String sha1 = HashService.calculateStringHash(data, "SHA-1");
                String sha256 = HashService.calculateStringHash(data, "SHA-256");
                String sha384 = HashService.calculateStringHash(data, "SHA-384");
                String sha512 = HashService.calculateStringHash(data, "SHA-512");

                String crc32 = HashService.calculateStringCRC32(data);

                addTextHash("MD5", md5, compare);
                addTextHash("SHA-1", sha1, compare);
                addTextHash("SHA-256", sha256, compare);
                addTextHash("SHA-384", sha384, compare);
                addTextHash("SHA-512", sha512, compare);

                addTextHash("CRC32", crc32, compare);
            }
        });
    }

    private void content_help() {
        Button btnWebsite = (Button) findViewById(R.id.ButtonWebsite);
        Button btnSupport = (Button) findViewById(R.id.ButtonSupport);

        btnWebsite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCodeDead();
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
                        .setChooserTitle("Send us an e-mail")
                        .startChooser();
            }
        });
    }

    private void content_about() {
        Button btnWebsite = (Button) findViewById(R.id.BtnWebsiteAbout);
        btnWebsite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCodeDead();
            }
        });
    }

    private void openCodeDead() {
        Uri uriUrl = Uri.parse("http://codedead.com/");
        Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
        startActivity(launchBrowser);
    }

    private void addFileHash(String hashName, String data, String compare) {
        if (hashName == null || hashName.length() == 0) return;
        if (data == null || data.length() == 0) return;

        EncryptionData encryptionData = new EncryptionData(hashName, data, compare);
        fileDataArrayList.add(encryptionData);
        mAdapterFile.notifyItemInserted(fileDataArrayList.size());
    }

    private void addTextHash(String hashName, String data, String compare) {
        if (hashName == null || hashName.length() == 0) return;
        if (data == null || data.length() == 0) return;

        EncryptionData encryptionData = new EncryptionData(hashName, data, compare);
        textDataArrayList.add(encryptionData);
        mAdapterText.notifyItemInserted(textDataArrayList.size());
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putInt("TAB_NUMBER", viewFlipper.getDisplayedChild());
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        LocaleHelper.onAttach(getBaseContext());

        viewFlipper.setDisplayedChild(3);
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
                    doubleBackToExitPressedOnce=false;
                }
            }, 2000);
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        int page = 0;
        //TODO: create all pages for content...

        if (id == R.id.nav_text) {
            page = 1;
        }  else if (id == R.id.nav_help) {
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
}
