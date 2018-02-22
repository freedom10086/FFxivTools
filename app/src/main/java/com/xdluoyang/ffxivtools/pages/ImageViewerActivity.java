package com.xdluoyang.ffxivtools.pages;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.xdluoyang.ffxivtools.R;
import com.xdluoyang.ffxivtools.widget.ZoomImageView;

public class ImageViewerActivity extends ActivityBase {

    public static boolean needAnimate = false;
    public static final String KEY_TILE = "key_title";
    public static final String KEY_SRC = "key_src";
    private static final String KEY_TRANS = "key_trans";

    public static void openWithAnimation(Activity activity, String title, String src, ImageView imgAvatar) {
        Intent intent = new Intent(activity, ImageViewerActivity.class);
        intent.putExtra(KEY_TILE, title);
        intent.putExtra(KEY_SRC, src);
        needAnimate = true;
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(activity, imgAvatar, KEY_TRANS);
        ActivityCompat.startActivity(activity, intent, options.toBundle());
    }

    public static void open(Context context, String title, String src) {
        Intent intent = new Intent(context, ImageViewerActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(KEY_TILE, title);
        intent.putExtra(KEY_SRC, src);
        needAnimate = false;
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }

        setContentView(R.layout.activity_image_viewer);
        String title = getIntent().getStringExtra(KEY_TILE);
        setToolBar(true, TextUtils.isEmpty(title) ? "浏览图片" : title);

        ZoomImageView imageView = findViewById(R.id.image);

        ViewCompat.setTransitionName(imageView, KEY_TRANS);
        Picasso.with(this).load(getIntent().getStringExtra(KEY_SRC)).into(imageView);
    }

    @Override
    public void finish() {
        super.finish();
        // 去掉自带的转场动画
        if (needAnimate) {
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        }
    }
}
