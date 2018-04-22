package com.xdluoyang.ffxivtools;

import android.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.xdluoyang.ffxivtools.pages.PageTools;

public class Main2Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        FrameLayout f = new FrameLayout(this);
        f.setLayoutParams(new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        f.setId(R.id.container);
        setContentView(f);

        Fragment to = new PageTools();
        getFragmentManager().beginTransaction().replace(R.id.container, to).commit();
    }
}
