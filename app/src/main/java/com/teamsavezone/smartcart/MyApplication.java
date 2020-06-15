package com.teamsavezone.smartcart;

import android.app.Application;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class MyApplication extends Application {

    public static Boolean initExp = false;
    public static Boolean addCart = false;
    public static String name;

    @Override
    public void onCreate() {
        super.onCreate();

        Realm.init(this);

        RealmConfiguration userConfig = new RealmConfiguration.Builder().build();

        Realm.setDefaultConfiguration(userConfig);
    }
}
