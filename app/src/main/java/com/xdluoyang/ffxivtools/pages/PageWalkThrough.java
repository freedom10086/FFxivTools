package com.xdluoyang.ffxivtools.pages;

import android.util.Log;

public class PageWalkThrough extends LazyPage{
    @Override
    public void onVisible(boolean isFirst) {
        Log.v("======", "PageWalkThrough onVisible:"+isFirst);
    }

    @Override
    public void onInVisible() {
        Log.v("======", "PageWalkThrough onInVisible");
    }
}
