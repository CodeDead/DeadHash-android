package com.codedead.deadhash.main;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.codedead.deadhash.domain.LocaleHelper;

public class Runner extends Application {

    @Override
    protected void attachBaseContext(Context base) {
        SharedPreferences sharedPref = base.getSharedPreferences("deadhashsettings", Context.MODE_PRIVATE);
        super.attachBaseContext(LocaleHelper.onAttach(base, sharedPref.getString("language", "en")));
    }
}
