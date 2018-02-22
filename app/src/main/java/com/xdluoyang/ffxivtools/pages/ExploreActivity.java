package com.xdluoyang.ffxivtools.pages;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.xdluoyang.ffxivtools.R;
import com.xdluoyang.ffxivtools.model.ExploreData;
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

public class ExploreActivity extends ActivityBase {

    private MyAdapter adapter;
    private List<ExploreData> datas = new ArrayList<>();
    private TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explore);

        setToolBar(true, "探索笔记");

        int width = (int) Util.getScreenWidthDP(this);
        RecyclerView recyclerView = findViewById(R.id.list_view);
        adapter = new MyAdapter();
        recyclerView.setAdapter(adapter);
        RecyclerView.LayoutManager manager = new GridLayoutManager(this, width / 70);
        recyclerView.setLayoutManager(manager);


        tabLayout = findViewById(R.id.tab);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        loadList();

    }


    private void loadList() {
        final Handler mainHandler = new Handler(getMainLooper());
        OkHttpClient client = new OkHttpClient();
        final Request request = new Request.Builder()
                .url("http://tools.ffxiv.cn/dajipai/csv/explore.csv")
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
                    ////小图代码,大图代码,顺序,名称,版本,地点,X,Y,天气,天气图片,时间,动作,动作代码,动作图片,备注,位置图片1,位置图片2
                    while ((line = reader.readLine()) != null) {
                        String[] tokens = Util.readCsv(17, line);
                        datas.add(new ExploreData(tokens[0], tokens[1], tokens[2], tokens[3], tokens[4],
                                tokens[5], tokens[6], tokens[7], tokens[8], tokens[9], tokens[10], tokens[11],
                                tokens[12], tokens[13], tokens[14], tokens[15], tokens[16]));
                    }

                    if (datas.size() > 1) {
                        datas.remove(0);
                        mainHandler.post(() -> adapter.notifyDataSetChanged());
                    }
                }
            }
        });
    }


    private class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {
        private int start;

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.row_grid_item, parent, false);

            return new MyViewHolder(v);
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            holder.itemView.setTag(start + position);

            Picasso.with(ExploreActivity.this)
                    .load("http://tools.ffxiv.cn/dajipai/tupian/explore/" + datas.get(start + position).id + ".png")
                    .into(holder.icon);
        }

        @Override
        public int getItemCount() {
            int count = 0;
            for (int i = 0; i < datas.size(); i++) {
                ExploreData d = datas.get(i);
                if (d.version == tabLayout.getSelectedTabPosition()) {
                    if (count == 0)
                        start = i;
                    count++;
                }
            }
            return count;
        }
    }

    private class MyViewHolder extends RecyclerView.ViewHolder {
        final ImageView icon;

        public MyViewHolder(final View itemView) {
            super(itemView);

            icon = itemView.findViewById(R.id.icon);

            itemView.setOnClickListener(view -> {
                int position = (int) itemView.getTag();
                Intent i = new Intent(ExploreActivity.this, ExploreDetailActivity.class);
                i.putExtra(ExploreDetailActivity.KEY_EXPLORE, datas.get(position));
                startActivity(i);
            });
        }
    }
}
