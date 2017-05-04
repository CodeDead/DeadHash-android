package com.codedead.deadline.deadhash.gui;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
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
import android.widget.ImageButton;
import android.widget.ViewFlipper;

import com.codedead.deadline.deadhash.R;
import com.codedead.deadline.deadhash.domain.FileAdapter;
import com.codedead.deadline.deadhash.domain.FileData;
import com.codedead.deadline.deadhash.domain.LocaleHelper;

import java.util.ArrayList;

//TODO: allow translations
public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private int currentPage;
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
        mRecyclerView = (RecyclerView) findViewById(R.id.file_recycler);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new FileAdapter(fileDataArrayList);
        mRecyclerView.setAdapter(mAdapter);

        ImageButton btnGenerate = (ImageButton) findViewById(R.id.ImgBtnFileData);

        btnGenerate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: generate hashes, depending on user settings
            }
        });
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
            super.onBackPressed();
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
