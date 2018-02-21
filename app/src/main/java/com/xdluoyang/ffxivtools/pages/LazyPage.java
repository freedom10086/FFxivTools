package com.xdluoyang.ffxivtools.pages;

public class LazyPage extends PageBase {

    private boolean isFirstVisible = true;
    private boolean isVisible = false;


    @Override
    public void onResume() {
        super.onResume();
        if (isFirstVisible) {
            notifyVisible(true);
        }else {
            notifyVisible(!isHidden());
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        notifyVisible(false);
    }

    public void notifyVisible(boolean isVisible) {
        if (isVisible == this.isVisible) return;

        if (isVisible) {
            onVisible(isFirstVisible);
            isFirstVisible = false;
            this.isVisible = true;
        } else {
            onInVisible();
            this.isVisible = false;
        }
    }

    public void onVisible(boolean isFirst) {
    }

    public void onInVisible() {
    }
}
