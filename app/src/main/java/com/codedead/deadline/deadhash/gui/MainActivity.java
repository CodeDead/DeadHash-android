package com.codedead.deadline.deadhash.gui;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.codedead.deadline.deadhash.R;
import com.codedead.deadline.deadhash.domain.FileAdapter;
import com.codedead.deadline.deadhash.domain.FileData;
import com.codedead.deadline.deadhash.domain.FileDialog;
import com.codedead.deadline.deadhash.domain.HashService;
import com.codedead.deadline.deadhash.domain.LocaleHelper;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private int currentPage;
    private boolean doubleBackToExitPressedOnce;

    private SharedPreferences sharedPreferences;

    private ViewFlipper viewFlipper;

    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private FileAdapter mAdapter;

    private ArrayList<FileData> fileDataArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        fileDataArrayList = new ArrayList<>();
        sharedPreferences = getApplicationContext().getSharedPreferences(getString(R.string.preferences_file_key), Context.MODE_PRIVATE);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        viewFlipper = (ViewFlipper) findViewById(R.id.vf);

        content_file();
    }

    private void content_file() {
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
        }

        mRecyclerView = (RecyclerView) findViewById(R.id.file_recycler);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new FileAdapter(fileDataArrayList);
        mRecyclerView.setAdapter(mAdapter);

        ImageButton btnOpenFile = (ImageButton) findViewById(R.id.ImgBtnFileData);
        final EditText edtPath = (EditText) findViewById(R.id.EdtFile_name);
        Button btnGenerate = (Button) findViewById(R.id.ButtonGenerateFile);

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
                fileDataArrayList.clear();
                mAdapter.notifyDataSetChanged();

                File file = new File(edtPath.getText().toString());
                if (!file.exists()) {
                    Toast.makeText(MainActivity.this, "File does not exist!", Toast.LENGTH_SHORT).show();
                    return;
                }

                String md5 = HashService.calculateHash(file, "MD5");
                String sha1 = HashService.calculateHash(file, "SHA-1");
                String sha256 = HashService.calculateHash(file, "SHA-256");
                String sha384 = HashService.calculateHash(file, "SHA-384");
                String sha512 = HashService.calculateHash(file, "SHA-512");

                String ripemd128 = HashService.calculateHash(file,"RIPEMD128");
                String ripemd160 = HashService.calculateHash(file,"RIPEMD160");

                addFileHash("MD5", md5);
                addFileHash("SHA-1", sha1);
                addFileHash("SHA-256", sha256);
                addFileHash("SHA-384", sha384);
                addFileHash("SHA-512", sha512);

                addFileHash("RIPEMD-128", ripemd128);
                addFileHash("RIPEMD-160", ripemd160);
            }
        });
    }

    private void addFileHash(String hashName, String data) {
        if (hashName == null || hashName.length() == 0) return;
        if (data == null || data.length() == 0) return;

        FileData fileData = new FileData(hashName, data);
        fileDataArrayList.add(fileData);
        mAdapter.notifyItemInserted(fileDataArrayList.size());
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
            Toast.makeText(this, "Press 'BACK' again to exit.", Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    doubleBackToExitPressedOnce=false;
                }
            }, 2000);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            currentPage = 3;
            viewFlipper.setDisplayedChild(currentPage);
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        //TODO: create all pages for content...

        if (id == R.id.nav_file) {
            currentPage = 0;
        } else if (id == R.id.nav_text) {
            currentPage = 1;
        } else if (id == R.id.nav_compare) {
            currentPage = 2;
        } else if (id == R.id.nav_manage) {
            currentPage = 3;
        } else if (id == R.id.nav_help) {
            currentPage = 4;
        } else if (id == R.id.nav_about) {
            currentPage = 5;
        }

        viewFlipper.setDisplayedChild(currentPage);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
