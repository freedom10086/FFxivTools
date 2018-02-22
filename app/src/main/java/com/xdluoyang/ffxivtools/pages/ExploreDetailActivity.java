package com.xdluoyang.ffxivtools.pages;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.xdluoyang.ffxivtools.R;
import com.xdluoyang.ffxivtools.model.ExploreData;
import com.xdluoyang.ffxivtools.model.PetMountData;

public class ExploreDetailActivity extends ActivityBase {

    public static final String KEY_EXPLORE = "key_explore";
    private ImageView imageView, imageView1, imageView2, weatherImage, actionImage;
    private TextView name, des, pos, xy, time, weather, action, actionCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explore_detail);

        ExploreData e = getIntent().getParcelableExtra(KEY_EXPLORE);
        if (e == null) {
            return;
        }

        setToolBar(true, e.num + " " + e.name);
        imageView = findViewById(R.id.image);
        imageView1 = findViewById(R.id.image1);
        imageView2 = findViewById(R.id.image2);
        weatherImage = findViewById(R.id.weather_image);
        actionImage = findViewById(R.id.action_image);

        name = findViewById(R.id.name);
        des = findViewById(R.id.des);
        pos = findViewById(R.id.pos);
        xy = findViewById(R.id.pos_xy);
        time = findViewById(R.id.time);
        weather = findViewById(R.id.weather);
        action = findViewById(R.id.action);
        actionCode = findViewById(R.id.action_code);

        Picasso.with(this).load("http://tools.ffxiv.cn/dajipai/tupian/explore/" + e.bigId + ".png").into(imageView);
        if (TextUtils.isEmpty(e.image1)) {
            imageView1.setVisibility(View.GONE);
        } else {
            imageView1.setVisibility(View.VISIBLE);
            String src = "http://tools.ffxiv.cn/dajipai/tupian/explore/" + e.image1 + ".jpg";
            Picasso.with(this).load(src).into(imageView1);
            imageView1.setOnClickListener(view -> ImageViewerActivity.openWithAnimation(ExploreDetailActivity.this, e.num + " " + e.name, src, imageView1));
        }

        if (TextUtils.isEmpty(e.image2)) {
            imageView2.setVisibility(View.GONE);
        } else {
            imageView2.setVisibility(View.VISIBLE);
            String src = "http://tools.ffxiv.cn/dajipai/tupian/explore/" + e.image2 + ".jpg";
            Picasso.with(this).load("http://tools.ffxiv.cn/dajipai/tupian/explore/" + e.image2 + ".jpg").into(imageView2);
            imageView2.setOnClickListener(view -> ImageViewerActivity.openWithAnimation(ExploreDetailActivity.this, e.num + " " + e.name, src, imageView2));
        }

        name.setText(e.num + " " + e.name);
        des.setText(e.comment);
        pos.setText(e.map);
        xy.setText("(X:" + e.x + ",Y:" + e.y + ")");

        if (TextUtils.isEmpty(e.time)) {
            time.setVisibility(View.GONE);
        } else {
            time.setVisibility(View.VISIBLE);
            time.setText(e.time);
        }

        if (TextUtils.isEmpty(e.wether)) {
            weather.setVisibility(View.GONE);
            weatherImage.setVisibility(View.GONE);
        } else {
            weather.setVisibility(View.VISIBLE);
            weatherImage.setVisibility(View.VISIBLE);

            weather.setText(e.wether);
            Picasso.with(this).load("http://tools.ffxiv.cn/dajipai/tupian/weather/" + e.wetherImage + ".png").into(weatherImage);
        }

        Picasso.with(this).load("http://tools.ffxiv.cn/dajipai/tupian/action/" + e.actionImage + ".png").into(actionImage);

        action.setText(e.action);
        actionCode.setText(e.actionCode);
    }
}
