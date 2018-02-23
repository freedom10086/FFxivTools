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
import android.widget.RadioGroup;
import android.widget.TextView;

import com.xdluoyang.ffxivtools.R;
import com.xdluoyang.ffxivtools.model.MusicData;
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

public class MusicActivity extends ActivityBase {

    private int start;
    private List<MusicData> datas = new ArrayList<>();
    private int currentIndex = 1, currentSelectMusicPos = -1;
    private final String[] names = new String[]{"区域场景", "迷宫挑战", "讨伐歼灭", "大型任务", "其他", "季节活动"};
    private TextView nameLabel, methodLabel, method;
    private MyAdapter myAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music);

        RecyclerView recyclerView = findViewById(R.id.list_view);
        myAdapter = new MyAdapter();
        recyclerView.setAdapter(myAdapter);
        RecyclerView.LayoutManager manager = new GridLayoutManager(this, (int) (Util.getScreenWidthDP(this) / 180));
        recyclerView.setLayoutManager(manager);

        nameLabel = findViewById(R.id.name);
        nameLabel.setText(names[currentIndex - 1]);

        methodLabel = findViewById(R.id.method_label);
        method = findViewById(R.id.method);

        RadioGroup group = findViewById(R.id.radioGroup);
        group.setOnCheckedChangeListener((radioGroup, i) -> {
            switch (i) {
                case R.id.btn1:
                    currentIndex = 1;
                    break;
                case R.id.btn2:
                    currentIndex = 2;
                    break;
                case R.id.btn3:
                    currentIndex = 3;
                    break;
                case R.id.btn4:
                    currentIndex = 4;
                    break;
                case R.id.btn5:
                    currentIndex = 5;
                    break;
                case R.id.btn6:
                    currentIndex = 6;
                    break;
                default:
                    currentIndex = 1;
                    break;
            }

            tabChange();
        });

        methodLabel.setVisibility(View.INVISIBLE);
        method.setVisibility(View.INVISIBLE);

        loadList();
    }

    private void loadList() {
        final Handler mainHandler = new Handler(getMainLooper());
        OkHttpClient client = new OkHttpClient();
        final Request request = new Request.Builder()
                .url("http://tools.ffxiv.cn/dajipai/csv/music.csv")
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
                    int num;
                    int type;

                    //编号,音乐,位置,坐标,NPC,其他,备注,类型,BGM
                    while ((line = reader.readLine()) != null) {
                        System.out.println(line);
                        String[] tokens = Util.readCsv(9, line);

                        if (!TextUtils.isEmpty(tokens[0]) && TextUtils.isDigitsOnly(tokens[0])) {
                            num = Integer.parseInt(tokens[0]);
                        } else {
                            num = 0;
                        }

                        if (!TextUtils.isEmpty(tokens[7]) && TextUtils.isDigitsOnly(tokens[7])) {
                            type = Integer.parseInt(tokens[7]);
                        } else {
                            type = 6;
                        }

                        datas.add(new MusicData(num, tokens[1], tokens[2], tokens[3], tokens[4],
                                tokens[5], tokens[6], type, tokens[8]));
                    }

                    if (datas.size() > 1) {
                        datas.remove(0);
                        Collections.sort(datas, (t0, t1) -> {
                            if (t0.type > t1.type) return 1;
                            else if (t0.type < t1.type) return -1;
                            else {
                                if (t0.num > t1.num) return 1;
                                else return -1;
                            }
                        });
                        mainHandler.post(() -> myAdapter.notifyDataSetChanged());
                    }
                }
            }
        });
    }

    private void tabChange() {
        nameLabel.setText(names[currentIndex - 1]);
        methodLabel.setVisibility(View.INVISIBLE);
        method.setVisibility(View.INVISIBLE);
        currentSelectMusicPos = -1;

        myAdapter.notifyDataSetChanged();
    }

    private class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {


        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.row_music_item, parent, false);

            return new MyViewHolder(v);
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            holder.itemView.setTag(position + start);

            holder.num.setText(String.format("%03d", datas.get(position + start).num));
            holder.name.setText(datas.get(position + start).name);
            if (position == currentSelectMusicPos) {
                holder.num.setTextColor(ContextCompat.getColor(MusicActivity.this, android.R.color.holo_blue_dark));
                holder.name.setTextColor(ContextCompat.getColor(MusicActivity.this, android.R.color.holo_blue_dark));
            } else {
                holder.num.setTextColor(ContextCompat.getColor(MusicActivity.this, android.R.color.darker_gray));
                holder.name.setTextColor(ContextCompat.getColor(MusicActivity.this, android.R.color.darker_gray));
            }
        }

        @Override
        public int getItemCount() {
            int count = 0;
            for (int i = 0; i < datas.size(); i++) {
                MusicData d = datas.get(i);
                if (d.type == currentIndex) {
                    if (count == 0)
                        start = i;
                    count++;
                }
            }
            return count;
        }
    }

    private class MyViewHolder extends RecyclerView.ViewHolder {
        final TextView num, name;

        public MyViewHolder(final View itemView) {
            super(itemView);

            num = itemView.findViewById(R.id.num);
            name = itemView.findViewById(R.id.name);


            itemView.setOnClickListener(view -> {
                int position = (int) itemView.getTag();
                MusicData d = datas.get(position);

                if (currentSelectMusicPos != position - start) {
                    int pre = currentSelectMusicPos;
                    currentSelectMusicPos = position - start;
                    if (pre >= 0)
                        myAdapter.notifyItemChanged(pre);

                    myAdapter.notifyItemChanged(currentSelectMusicPos);
                }

                methodLabel.setVisibility(View.VISIBLE);
                method.setVisibility(View.VISIBLE);
                String methodS = String.format("%03d", d.num) + " - " + d.name + "\n";
                String pos = (TextUtils.isEmpty(d.pos) ? "" : d.pos) + (TextUtils.isEmpty(d.posXy) ? "" : (":" + d.posXy));
                methodS += pos;
                // TODO music
                //src="//music.163.com/outchain/player?type=2&id=' + csvList[i][8] + '&auto=0&height=90"

                //编号,音乐,位置,坐标,NPC,其他,备注,类型,BGM
                if (!TextUtils.isEmpty(pos)) {
                    methodS += "\n";
                }

                if (TextUtils.isEmpty(d.npc)) {
                    methodS += d.others + (!TextUtils.isEmpty(d.comment) ? "(" + d.comment + ")" : "");
                } else {
                    methodS += "NPC:" + d.npc + "购买（" + d.others + "）";
                }

                method.setText(methodS);
            });
        }
    }
}
