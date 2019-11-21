package com.xdluoyang.ffxivtools.pages;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.xdluoyang.ffxivtools.R;
import com.xdluoyang.ffxivtools.model.BlueMagicData;
import com.xdluoyang.ffxivtools.util.Util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class BlueMagicActivity extends BaseActivity {

    private MyAdapter adapter;
    private List<BlueMagicData> datas = new ArrayList<>(128);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blue_magic);

        setToolBar(true, "青魔法书");

        int width = (int) Util.getScreenWidthDP(this);
        RecyclerView recyclerView = findViewById(R.id.list_view);
        adapter = new MyAdapter();
        recyclerView.setAdapter(adapter);
        RecyclerView.LayoutManager manager = new GridLayoutManager(this, width / 70);
        recyclerView.setLayoutManager(manager);

        loadList();
    }

    private void loadList() {
        final Handler mainHandler = new Handler(getMainLooper());
        OkHttpClient client = new OkHttpClient();
        final Request request = new Request.Builder()
                .url("https://tools.ffxiv.cn/lajipai/csv/qm.csv")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(response.body().byteStream()));
                    String line = "";
                    //编号,小图标,大图标,名称,类型,属性,稀有度,获得方法,类别,技能效果,敌人等级
                    while ((line = reader.readLine()) != null) {
                        String[] tokens = Util.readCsv(11, line);
                        if (TextUtils.isEmpty(tokens[0]) || !TextUtils.isDigitsOnly(tokens[0])) {
                            Log.w("BlueMagicActivity", "invalid line, " + line);
                            continue;
                        }
                        datas.add(new BlueMagicData(Integer.parseInt(tokens[0]), tokens[1], tokens[2], tokens[3], tokens[4],
                                tokens[5], tokens[6], tokens[7], tokens[8], tokens[9], tokens[10]));
                    }
                    mainHandler.post(() -> adapter.notifyDataSetChanged());
                }
            }
        });
    }


    private class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.row_grid_item, parent, false);

            return new MyViewHolder(v);
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            holder.itemView.setTag(position);
            Glide.with(findViewById(R.id.list_view)).load("https://tools.ffxiv.cn/lajipai/image/qingmo-ui/"
                    + datas.get(position).imgSmall + ".png")
                    .into(holder.icon);
        }

        @Override
        public int getItemCount() {
            return datas.size();
        }
    }

    private class MyViewHolder extends RecyclerView.ViewHolder {
        final ImageView icon;

        public MyViewHolder(final View itemView) {
            super(itemView);

            icon = itemView.findViewById(R.id.icon);

            itemView.setOnClickListener(view -> {
                int position = (int) itemView.getTag();
                Intent i = new Intent(BlueMagicActivity.this, BlueMagicDetailActivity.class);
                i.putExtra(BlueMagicDetailActivity.KEY_MAGIC, datas.get(position));
                startActivity(i);
            });
        }
    }
}
