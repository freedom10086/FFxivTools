package com.xdluoyang.ffxivtools;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.view.MenuItem;

import com.xdluoyang.ffxivtools.pages.ActivityBase;
import com.xdluoyang.ffxivtools.pages.LazyPage;
import com.xdluoyang.ffxivtools.pages.PageHome;
import com.xdluoyang.ffxivtools.pages.PageTools;
import com.xdluoyang.ffxivtools.pages.PageWalkThrough;

public class MainActivity extends ActivityBase {

    private int currentPage = 0;
    private LazyPage[] pages = new LazyPage[3];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState != null) {
            for (int i = 0; i < pages.length; i++)
                pages[i] = (LazyPage) getFragmentManager().getFragment(savedInstanceState, "page" + i);
        }

        if (pages[0] == null)
            pages[0] = new PageHome();
        if (pages[1] == null)
            pages[1] = new PageTools();
        if (pages[2] == null)
            pages[2] = new PageWalkThrough();

        for (int i = 0; i < pages.length; i++) {
            if (!pages[i].isAdded()) {
                //getFragmentManager().beginTransaction().add(R.id.main_view, pages[i], "page" + i).hide(pages[i]).commit();
            }
        }

        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(item -> {
            int pos;
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    pos = 0;
                    break;
                case R.id.navigation_tools:
                    pos = 1;
                    break;
                case R.id.navigation_walkthrough:
                    pos = 2;
                    break;
                default:
                    return false;
            }

            switchPage(pos);
            return true;
        });

        if (savedInstanceState == null)
            switchPage(0);
    }

    //https://stackoverflow.com/questions/15313598/once-for-all-how-to-correctly-save-instance-state-of-fragments-in-back-stack
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        for (int i = 0; i < pages.length; i++) {
            getFragmentManager().putFragment(outState, "page" + i, pages[i]);
        }
    }

    private void switchPage(int position) {
        LazyPage page = pages[position];
        FragmentManager manager = getFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();

        if (page.isAdded()) { // if the fragment is already in container
            transaction.show(page);
        } else { // fragment needs to be added to frame container
            transaction.add(R.id.main_view, page, "page" + position);
        }
        page.notifyVisible(true);

        for (int i = 0; i < pages.length; i++) {
            if (i != position && pages[i].isAdded()) {
                transaction.hide(pages[i]);
                pages[i].notifyVisible(false);
            }
        }

        transaction.commit();
        currentPage = position;
    }
}
