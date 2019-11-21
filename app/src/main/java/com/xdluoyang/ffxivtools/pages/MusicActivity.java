package com.xdluoyang.ffxivtools.pages;

import android.os.Bundle;
import android.os.Handler;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

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

public class MusicActivity extends BaseActivity {

    private int start;
    private List<MusicData> datas = new ArrayList<>();
    private int currentIndex = 1, currentSelectMusicPos = -1;
    private final String[] names = new String[]{"区域场景", "迷宫挑战", "讨伐歼灭", "大型任务", "其他", "季节活动", "商城购买"};
    private TextView nameLabel, methodLabel, method;
    private MyAdapter myAdapter;
    private View playBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music);

        setToolBar(true, "乐谱一览");

        RecyclerView recyclerView = findViewById(R.id.list_view);
        myAdapter = new MyAdapter();
        recyclerView.setAdapter(myAdapter);
        RecyclerView.LayoutManager manager = new GridLayoutManager(this, (int) (Util.getScreenWidthDP(this) / 180));
        recyclerView.setLayoutManager(manager);

        nameLabel = findViewById(R.id.name);
        nameLabel.setText(names[currentIndex - 1]);

        methodLabel = findViewById(R.id.method_label);
        method = findViewById(R.id.method);
        playBtn = findViewById(R.id.play_btn);

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
                case R.id.btn7:
                    currentIndex = 7;
                    break;
                default:
                    currentIndex = 1;
                    break;
            }

            tabChange();
        });

        playBtn.setVisibility(View.INVISIBLE);
        playBtn.setTag(-1);
        playBtn.setOnClickListener(view -> {
            int pos = (int) playBtn.getTag();
            if (pos >= 0) {
                if (!TextUtils.isEmpty(datas.get(pos).musicId))
                    Util.openBroswer(MusicActivity.this, "https://music.163.com/#/song?id=" + datas.get(pos).musicId);
                else
                    Toast.makeText(MusicActivity.this, "暂无试听", Toast.LENGTH_SHORT).show();
                //src="//music.163.com/outchain/player?type=2&id=' + csvList[i][8] + '&auto=0&height=90"
            }

        });

        methodLabel.setVisibility(View.INVISIBLE);
        method.setVisibility(View.INVISIBLE);

        loadList();
    }

    private void loadList() {
        final Handler mainHandler = new Handler(getMainLooper());
        OkHttpClient client = new OkHttpClient();
        final Request request = new Request.Builder()
                .url("https://tools.ffxiv.cn/lajipai/csv/music.csv")
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
                        //System.out.println(line);
                        String[] tokens = Util.readCsv(7, line);

                        if (!TextUtils.isEmpty(tokens[3]) && TextUtils.isDigitsOnly(tokens[3])) {
                            num = Integer.parseInt(tokens[3]);
                        } else {
                            num = 0;
                        }

                        if (!TextUtils.isEmpty(tokens[4]) && TextUtils.isDigitsOnly(tokens[4])) {
                            type = Integer.parseInt(tokens[4]);
                        } else {
                            type = 4; // others
                        }

                        datas.add(new MusicData(num, tokens[1], tokens[2], type, tokens[5], tokens[6]));
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
        playBtn.setVisibility(View.INVISIBLE);
        playBtn.setTag(-1);

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
                playBtn.setVisibility(View.VISIBLE);

                String methodS = String.format("%03d", d.num) + " - " + d.name + "\n" + d.method;
                playBtn.setTag(position);
                method.setText(methodS);
            });
        }
    }
}
