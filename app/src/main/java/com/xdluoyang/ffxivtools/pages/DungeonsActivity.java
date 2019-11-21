package com.xdluoyang.ffxivtools.pages;

import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.xdluoyang.ffxivtools.R;
import com.xdluoyang.ffxivtools.model.DungeonData;
import com.xdluoyang.ffxivtools.util.Util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class DungeonsActivity extends BaseActivity {

    private TextView nameLabel;
    private int currentIndex = 0;
    private RadioGroup group;
    private MyAdapter myAdapter;
    private List<DungeonData> datas = new ArrayList<>(200);
    private int start = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dungeons);

        setToolBar(true, "副本一览");

        RecyclerView recyclerView = findViewById(R.id.list_view);
        myAdapter = new MyAdapter();
        recyclerView.setAdapter(myAdapter);
        RecyclerView.LayoutManager manager = new GridLayoutManager(this, (int) (Util.getScreenWidthDP(this) / 180));
        recyclerView.setLayoutManager(manager);

        nameLabel = findViewById(R.id.name);

        group = findViewById(R.id.radioGroup);
        group.setOnCheckedChangeListener((radioGroup, i) -> {
            for (int index = 0; index < group.getChildCount(); index++) {
                if (group.getChildAt(index).getId() == i) {
                    currentIndex = index;
                    break;
                }
            }
            tabChange();
        });

        loadList();
    }

    private void tabChange() {
        View v = group.getChildAt(currentIndex);
        nameLabel.setText(v.getContentDescription());
        start = 0;

        myAdapter.notifyDataSetChanged();
    }

    private void loadList() {
        final Handler mainHandler = new Handler(getMainLooper());
        OkHttpClient client = new OkHttpClient();
        final Request request = new Request.Builder()
                .url("https://tools.ffxiv.cn/lajipai/csv/dungeons.csv")
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
                    int sort;
                    int type;
                    int belong;

                    //编码,顺序,副本名称,任务,位置,NPC,地图map,装等限制,等级,所属,类型
                    while ((line = reader.readLine()) != null) {
                        System.out.println(line);
                        String[] tokens = Util.readCsv(11, line);

                        if (!TextUtils.isEmpty(tokens[1]) && TextUtils.isDigitsOnly(tokens[1])) {
                            sort = Integer.parseInt(tokens[1]);
                        } else {
                            sort = Integer.MAX_VALUE;
                        }

                        if (!TextUtils.isEmpty(tokens[10]) && TextUtils.isDigitsOnly(tokens[10])) {
                            type = Integer.parseInt(tokens[10]);
                        } else {
                            type = 10;
                        }

                        if (!TextUtils.isEmpty(tokens[9]) && TextUtils.isDigitsOnly(tokens[9])) {
                            belong = Integer.parseInt(tokens[9]);
                        } else {
                            belong = 0;
                        }

                        datas.add(new DungeonData(tokens[0], sort, tokens[2], tokens[3], tokens[4],
                                tokens[6], tokens[5], tokens[7], tokens[8], belong, type));
                    }

                    if (datas.size() > 1) {
                        datas.remove(0);
                        Collections.sort(datas, (t0, t1) -> {
                            if (t0.type > t1.type) return 1;
                            else if (t0.type < t1.type) return -1;
                            else {
                                if (t0.belong > t1.belong) return 1;
                                else if (t0.belong < t1.belong) return -1;
                                else {
                                    return Integer.compare(t0.sort, t1.sort);
                                }

                            }
                        });
                        mainHandler.post(() -> myAdapter.notifyDataSetChanged());
                    }
                }
            }
        });


    }

    private class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.row_dungeons, parent, false);

            return new MyViewHolder(v);
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            holder.itemView.setTag(position + start);

            Glide.with(DungeonsActivity.this)
                    .load("https://tools.ffxiv.cn/lajipai/image/dungeons/" + datas.get(position + start).id + ".png")
                    .into(holder.image);
            holder.name.setText(datas.get(position + start).name);
        }

        @Override
        public int getItemCount() {
            if (currentIndex == 12) {
                start = 0;
                return datas.size();
            }

            int belong = currentIndex % 4 + 2;
            int type = currentIndex / 4 + 1;

            int count = 0;
            for (int i = 0; i < datas.size(); i++) {
                DungeonData d = datas.get(i);
                if (d.type == type && d.belong == belong) {
                    if (count == 0)
                        start = i;
                    count++;
                }
            }
            return count;
        }
    }

    private class MyViewHolder extends RecyclerView.ViewHolder {
        final TextView name;
        final ImageView image;

        public MyViewHolder(final View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id.image);
            name = itemView.findViewById(R.id.name);

            itemView.setOnClickListener(view -> {
                int position = (int) itemView.getTag();
                DungeonData d = datas.get(position);
                DungeonDetailActivity.openWithAnimation(DungeonsActivity.this, d, image);
            });
        }
    }
}
