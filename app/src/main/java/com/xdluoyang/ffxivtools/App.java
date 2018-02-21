package com.xdluoyang.ffxivtools;

import android.app.Application;

import com.xdluoyang.ffxivtools.huntapi.Client;

/**
 * Created by Admin on 2018/2/20.
 */

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        Client.init(getApplicationContext());
    }
}
