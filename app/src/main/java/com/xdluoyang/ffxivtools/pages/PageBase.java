package com.xdluoyang.ffxivtools.pages;

import android.app.Fragment;
import android.content.Intent;


public class PageBase extends Fragment{

    protected void switchActivity(Class<?> cls) {
        getActivity().startActivity(new Intent(getActivity(), cls));
    }
}
