package com.xdluoyang.ffxivtools.util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.DisplayMetrics;
import android.util.Log;


public class Util {
    public static final float getScreenWidthDP(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float dpHeight = displayMetrics.heightPixels / displayMetrics.density;
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;

        return dpWidth;
    }

    public static final float getScreenWidthPixel(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float dpHeight = displayMetrics.heightPixels;
        float dpWidth = displayMetrics.widthPixels;

        return dpWidth;
    }

    public static String[] readCsv(int tokenCount, String l) {
        int index = 0;
        int start = 0;
        boolean isIn = false;
        String[] tokens = new String[tokenCount];

        for (int i = 0; i < l.length(); i++) {
            if (l.charAt(i) == '"') {
                if (!isIn && (i == 0 || l.charAt(i - 1) == ',')) {
                    start = i;
                    isIn = true;
                } else if (isIn && (i == l.length() - 1 || l.charAt(i + 1) == ',')) {
                    isIn = false;
                }
            } else if (isIn) {
                continue;
            } else if (l.charAt(i) == ',') {
                int end = i;
                if (index >= tokenCount) {
                    Log.e("==err==", l);
                    break;
                }
                if (start == end) {
                    tokens[index] = "";
                } else {
                    tokens[index] = l.substring(start, end);
                }

                start = i + 1;
                index++;
            } else if (i == l.length() - 1) { //到最后了
                int end = l.length();
                if (start < end) {
                    if (index >= tokenCount) {
                        Log.e("==err==", l);
                        break;
                    }
                    tokens[index] = l.substring(start, end);
                }
            }
        }

        for (int k = 0; k < tokenCount - index - 1; k++) {
            tokens[k + index + 1] = "";
        }

        return tokens;
    }

    public static void openBroswer(Context activity, String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri content_url = Uri.parse(url);
        intent.setData(content_url);
        activity.startActivity(intent);
    }
}
