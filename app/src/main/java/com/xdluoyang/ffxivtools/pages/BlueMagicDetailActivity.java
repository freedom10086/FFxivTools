package com.xdluoyang.ffxivtools.pages;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.xdluoyang.ffxivtools.R;
import com.xdluoyang.ffxivtools.model.BlueMagicData;
import com.xdluoyang.ffxivtools.model.PetMountData;

public class BlueMagicDetailActivity extends BaseActivity {

    public static final String KEY_MAGIC = "blue_magic";

    private ImageView image;
    private TextView name, shuxin, type, method, description;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blue_magic_detail);

        BlueMagicData p = getIntent().getParcelableExtra(KEY_MAGIC);
        if (p == null) {
            return;
        }

        setToolBar(true, "NO." + p.num + " " + p.name);

        image = findViewById(R.id.image);
        name = findViewById(R.id.name);
        shuxin = findViewById(R.id.shuxin);
        type = findViewById(R.id.type);
        method = findViewById(R.id.method);
        description = findViewById(R.id.des);

        Glide.with(this)
                .load("https://tools.ffxiv.cn/lajipai/image/qingmo-ui/" + p.imgBig + ".png")
                .into(image);
        name.setText(p.name + "   " + p.xiYou);
        shuxin.setText("攻击属性：" + p.attribute);
        type.setText("攻击类型：" + p.type);
        method.setText(TextUtils.isEmpty(p.method) ? "暂无" : p.method);
        description.setText(p.effect);

        // TODO
        // p.methodType 0-qingmo.png  -96px -61px  width:22px;height:22px
        // -57px -133px
        // -74px -86px
    }
}