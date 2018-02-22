package com.xdluoyang.ffxivtools.pages;

import android.app.Activity;
import android.os.Bundle;

import com.squareup.picasso.Picasso;
import com.xdluoyang.ffxivtools.R;
import com.xdluoyang.ffxivtools.widget.HotMapView;

public class TestActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        HotMapView imageView = findViewById(R.id.view);
        Picasso.with(this).load("https://hunt.ffxiv.xin/img/map/500/EasternThanalan.png").into(imageView);

    }
}
