package com.xdluoyang.ffxivtools.pages;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.xdluoyang.ffxivtools.R;
import com.xdluoyang.ffxivtools.model.PetMountData;

public class PetMountDetailActivity extends ActivityBase {

    public static final String KEY_PET = "key_pet";

    private ImageView image, fly;
    private TextView name, method, description;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pet_mount_detail);

        PetMountData p = getIntent().getParcelableExtra(KEY_PET);
        if (p == null) {
            return;
        }

        setToolBar(true, p.name + " (" + p.version + ")");

        image = findViewById(R.id.image);
        name = findViewById(R.id.name);
        fly = findViewById(R.id.fly);
        method = findViewById(R.id.method);
        description = findViewById(R.id.des);

        Picasso.with(this)
                .load("http://tools.ffxiv.cn/dajipai/tupian/chongwuzuoqi/" + p.bigId + ".png")
                .into(image);
        name.setText(p.name);
        fly.setVisibility(p.fly ? View.VISIBLE : View.INVISIBLE);
        method.setText(TextUtils.isEmpty(p.method) ? "暂无" : p.method);
        description.setText(TextUtils.isEmpty(p.description) ? "暂无" : p.description);
    }
}
