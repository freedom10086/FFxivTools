package com.xdluoyang.ffxivtools.util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
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

    public static String[] readCsv(int tokenCount, String content) {
        int index = 0;
        int start = 0;
        boolean isIn = false;
        String[] tokens = new String[tokenCount];

        for (int i = 0; i < content.length(); i++) {
            if (content.charAt(i) == '"') {
                if (!isIn && (i == 0 || content.charAt(i - 1) == ',')) {
                    start = i;
                    isIn = true;
                } else if (isIn && (i == content.length() - 1 || content.charAt(i + 1) == ',')) {
                    isIn = false;
                }
            } else if (isIn) {
                continue;
            } else if (content.charAt(i) == ',') {
                int end = i;
                if (index >= tokenCount) {
                    Log.e("==err==", content);
                    break;
                }
                if (start == end) {
                    tokens[index] = "";
                } else {
                    tokens[index] = content.substring(start, end);
                }

                start = i + 1;
                index++;
            } else if (i == content.length() - 1) { //到最后了
                int end = content.length();
                if (start < end) {
                    if (index >= tokenCount) {
                        Log.e("==err==", content);
                        break;
                    }
                    tokens[index] = content.substring(start, end);
                }
            }
        }

        for (int k = 0; k < tokenCount - index - 1; k++) {
            tokens[k + index + 1] = "";
        }

        for (int i = 0; i < tokens.length; i++) {
            if (!TextUtils.isEmpty(tokens[i]) && tokens[i].contains("br")) {
                tokens[i] = tokens[i].replaceAll("<br\\s?/>", "\n");
            }
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
