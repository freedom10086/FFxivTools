package com.xdluoyang.ffxivtools.pages;

import android.util.Log;

/**
 * Created by Admin on 2018/2/20.
 */

public class PageHome extends LazyPage {
    @Override
    public void onVisible(boolean isFirst) {
        Log.v("======", "PageHome onVisible:"+isFirst);

    }

    @Override
    public void onInVisible() {
        Log.v("======", "PageHome onInVisible");
    }
}
