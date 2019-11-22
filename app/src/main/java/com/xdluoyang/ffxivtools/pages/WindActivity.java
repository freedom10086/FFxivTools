package com.xdluoyang.ffxivtools.pages;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.Snackbar;
import com.xdluoyang.ffxivtools.R;
import com.xdluoyang.ffxivtools.model.WindData;
import com.xdluoyang.ffxivtools.util.Util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
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
        GridLayoutManager manager = new GridLayoutManager(this, width / 180);
        manager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (position % 7 == 0) {
                    return width / 180;
                }
                return 1;
            }
        });
        recyclerView.setLayoutManager(manager);

        loadList();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_wind_help, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.help:  // TODO
                try {
                    StringBuilder sb = new StringBuilder();
                    InputStream is = getAssets().open("wind_help.txt");
                    BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
                    String str;
                    while ((str = br.readLine()) != null) {
                        sb.append(str);
                    }
                    br.close();
                    Log.i("==", sb.toString());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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


    private class MyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private final Integer TYPE_TITLE = 0;

        private final Integer TYPE_CONTENT = 1;

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType == TYPE_CONTENT) {
                View v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.row_winds_item, parent, false);
                return new MyViewHolder(v);
            } else {
                View v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.row_section_title_item, parent, false);
                return new MyTitleViewHolder(v);
            }
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if (holder instanceof MyViewHolder) {
                int pos = position - (position / 7) - 1;
                holder.itemView.setTag(pos);
                Glide.with(findViewById(R.id.list_view)).load("https://tools.ffxiv.cn/lajipai/image/aether/"
                        + (pos + 1) + ".png")
                        .into(((MyViewHolder) holder).image);
            } else {
                String title = (position / 7 + 3) + ".0区域";
                ((MyTitleViewHolder) holder).title.setText(title);
            }
        }

        @Override
        public int getItemViewType(int position) {
            if (position % 7 == 0) {
                return TYPE_TITLE;
            }
            return TYPE_CONTENT;
        }

        @Override
        public int getItemCount() {
            return datas.size() + 3;
        }
    }

    private class MyViewHolder extends RecyclerView.ViewHolder {
        final ImageView image;

        public MyViewHolder(final View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id.image);

            itemView.setOnClickListener(view -> {
                int position = (int) itemView.getTag();
                if (TextUtils.isEmpty(datas.get(position).mapId)) {
                    Snackbar.make(itemView, "该地图风脉全部由主线触发！", 2000).show();
                    return;
                }
                Intent i = new Intent(WindActivity.this, WindDetailActivity.class);
                i.putExtra(WindDetailActivity.KEY_WIND, datas.get(position));
                startActivity(i);
            });
        }
    }

    private class MyTitleViewHolder extends RecyclerView.ViewHolder {

        final TextView title;

        public MyTitleViewHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.section_title);
        }
    }
}
