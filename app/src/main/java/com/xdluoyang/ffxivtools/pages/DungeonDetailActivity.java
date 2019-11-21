package com.xdluoyang.ffxivtools.pages;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.view.ViewCompat;
import androidx.appcompat.widget.Toolbar;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.xdluoyang.ffxivtools.R;
import com.xdluoyang.ffxivtools.model.DungeonData;

public class DungeonDetailActivity extends BaseActivity {

    public static final String KEY_DUNGEON = "key_dungeon";
    private static final String NAME_IMG = "image";
    private ImageView image;


    private CollapsingToolbarLayout toolbarLayout;

    public static void openWithAnimation(Activity activity, DungeonData d, ImageView image) {
        Intent intent = new Intent(activity, DungeonDetailActivity.class);
        intent.putExtra(KEY_DUNGEON, d);

        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(activity, image, NAME_IMG);
        ActivityCompat.startActivity(activity, intent, options.toBundle());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dungeon_detail);
        DungeonData d = getIntent().getParcelableExtra(KEY_DUNGEON);
        if (d == null) {
            return;
        }

        image = findViewById(R.id.image);
        Picasso.get()
                .load("https://tools.ffxiv.cn/lajipai/image/dungeons/" + d.id + ".png")
                .into(image);
        ViewCompat.setTransitionName(image, NAME_IMG);
        toolbarLayout = findViewById(R.id.toolbar_layout);
        toolbarLayout.setTitle(d.name);

        Toolbar mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        TextView method = findViewById(R.id.method);
        ////编码,顺序,所属,副本名称,任务,位置,坐标,NPC,装等限制,类型,是否主线
        String methods = "";
        methods += "任务：" + d.renwu + "\n";
        methods += "位置：" + d.pos + "\n";
        methods += "NPC：" + d.npc + "\n";
        methods += "等级：" + d.level + "\n";
        methods += "装等：" + d.zhuandeng + "\n";

        method.setText(methods);
    }

    @Override
    public void finish() {
        super.finish();
        // 去掉自带的转场动画
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }
}
