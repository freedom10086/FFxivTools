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
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.xdluoyang.ffxivtools.R;
import com.xdluoyang.ffxivtools.model.BlueMagicData;
import com.xdluoyang.ffxivtools.model.WindData;
import com.xdluoyang.ffxivtools.util.Util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class WindActivity extends BaseActivity {

    private MyAdapter adapter;
    private List<WindData> datas = new ArrayList<>(64);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wind);

        setToolBar(true, "风脉泉一览");

        int width = (int) Util.getScreenWidthDP(this);
        RecyclerView recyclerView = findViewById(R.id.list_view);
        adapter = new MyAdapter();
        recyclerView.setAdapter(adapter);
        RecyclerView.LayoutManager manager = new GridLayoutManager(this, width / 180);
        recyclerView.setLayoutManager(manager);

        loadList();
    }

    private void loadList() {
        final Handler mainHandler = new Handler(getMainLooper());
        OkHttpClient client = new OkHttpClient();
        final Request request = new Request.Builder()
                .url("https://tools.ffxiv.cn/lajipai/csv/aether.csv")
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
                    // mapid name [] * 14 pos ((16.2　14.2)Z:0.6)
                    while ((line = reader.readLine()) != null) {
                        String[] tokens = Util.readCsv(16, line);
                        String id = tokens[0];
                        String name = tokens[1];
                        List<String> posList = Arrays.asList(Arrays.copyOfRange(tokens, 2, tokens.length));
                        WindData d = new WindData(id, name, posList);
                        datas.add(d);
                    }
                    if (datas.size() > 0) {
                        datas.remove(0);
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
                    .inflate(R.layout.row_windows_item, parent, false);

            return new MyViewHolder(v);
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            holder.itemView.setTag(position);
            Glide.with(findViewById(R.id.list_view)).load("https://tools.ffxiv.cn/lajipai/image/aether/"
                    + (position + 1) + ".png")
                    .into(holder.image);
        }

        @Override
        public int getItemCount() {
            return datas.size();
        }
    }

    private class MyViewHolder extends RecyclerView.ViewHolder {
        final ImageView image;

        public MyViewHolder(final View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id.image);

            itemView.setOnClickListener(view -> {
                int position = (int) itemView.getTag();
                Intent i = new Intent(WindActivity.this, BlueMagicDetailActivity.class);
                i.putExtra(BlueMagicDetailActivity.KEY_MAGIC, datas.get(position));
                startActivity(i);
            });
        }
    }
}
