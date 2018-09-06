package com.xdluoyang.ffxivtools.pages;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.xdluoyang.ffxivtools.R;
import com.xdluoyang.ffxivtools.model.DungeonData;
import com.xdluoyang.ffxivtools.model.MusicData;
import com.xdluoyang.ffxivtools.util.Util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class DungeonsActivity extends ActivityBase {

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
            switch (i) {
                case R.id.btn1:
                    currentIndex = 0;
                    break;
                case R.id.btn2:
                    currentIndex = 1;
                    break;
                case R.id.btn3:
                    currentIndex = 2;
                    break;
                case R.id.btn4:
                    currentIndex = 3;
                    break;
                case R.id.btn5:
                    currentIndex = 4;
                    break;
                case R.id.btn6:
                    currentIndex = 5;
                    break;
                case R.id.btn7:
                    currentIndex = 6;
                    break;
                case R.id.btn8:
                    currentIndex = 7;
                    break;
                case R.id.btn9:
                    currentIndex = 8;
                    break;
                case R.id.btn10:
                    currentIndex = 9;
                    break;
                default:
                    currentIndex = 0;
                    break;
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
                .url("http://tools.ffxiv.cn/dajipai/csv/dungeons.csv")
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

                    //编码,顺序,所属,副本名称,任务,位置,坐标,NPC,装等限制,类型,是否主线
                    while ((line = reader.readLine()) != null) {
                        System.out.println(line);
                        String[] tokens = Util.readCsv(11, line);

                        if (!TextUtils.isEmpty(tokens[1]) && TextUtils.isDigitsOnly(tokens[1])) {
                            sort = Integer.parseInt(tokens[1]);
                        } else {
                            sort = Integer.MAX_VALUE;
                        }

                        if (!TextUtils.isEmpty(tokens[9]) && TextUtils.isDigitsOnly(tokens[9])) {
                            type = Integer.parseInt(tokens[9]);
                        } else {
                            type = 10;
                        }

                        datas.add(new DungeonData(tokens[0], sort, tokens[3], tokens[4], tokens[5],
                                tokens[6], tokens[7], tokens[8], type, Objects.equals(tokens[10], "1")));
                    }

                    if (datas.size() > 1) {
                        datas.remove(0);
                        Collections.sort(datas, (t0, t1) -> {
                            if (t0.type > t1.type) return 1;
                            else if (t0.type < t1.type) return -1;
                            else {
                                if (t0.sort > t1.sort) return 1;
                                else return -1;
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

            Picasso.get()
                    .load("http://tools.ffxiv.cn/dajipai/tupian/dungeons/" + datas.get(position + start).id + ".png")
                    .into(holder.image);

            holder.name.setText(datas.get(position + start).name);
        }

        @Override
        public int getItemCount() {
            int count = 0;
            for (int i = 0; i < datas.size(); i++) {
                DungeonData d = datas.get(i);
                if (d.type == currentIndex + 1) {
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
