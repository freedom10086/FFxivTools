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
import com.xdluoyang.ffxivtools.model.PetMountData;
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


public class PetsActivity extends BaseActivity {

    private MyAdapter adapter;
    private List<PetMountData> datas = new ArrayList<>(300);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pets);

        setToolBar(true, "宠物一览");

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
                .url("https://tools.ffxiv.cn/lajipai/csv/pets.csv")
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
                    StringBuilder preLine = new StringBuilder();
                    String line = "";

                    List<String> lines = new ArrayList<>(300);
                    while ((line = reader.readLine()) != null) {
                        if (!line.matches("^([0-9]+\\.tex).*$")) {
                            preLine.append(line);
                            continue;
                        } else {
                            lines.add(preLine.toString());
                            preLine = new StringBuilder(line);
                        }
                    }

                    if (!TextUtils.isEmpty(preLine.toString()))
                        lines.add(preLine.toString());

                    int tokenCount = 8;

                    String[] tokens = new String[tokenCount];
                    boolean isIn = false;
                    int index = 0;
                    int start = 0;

                    for (String l : lines) {
                        index = 0;
                        start = 0;
                        isIn = false;

                        for (int i = 0; i < l.length(); i++) {
                            if (l.charAt(i) == '"') {
                                if (!isIn && (i == 0 || l.charAt(i - 1) == ',')) {
                                    start = i;
                                    isIn = true;
                                } else if (isIn && (i == l.length() - 1 || l.charAt(i + 1) == ',')) {
                                    isIn = false;
                                }
                            } else if (isIn) {
                                continue;
                            } else if (l.charAt(i) == ',') {
                                int end = i;
                                if (index >= tokenCount) {
                                    Log.e("==err==", l);
                                    break;
                                }
                                if (start == end) {
                                    tokens[index] = "";
                                } else {
                                    tokens[index] = l.substring(start, end);
                                }

                                start = i + 1;
                                index++;
                            } else if (i == l.length() - 1) { //到最后了
                                int end = l.length();
                                if (start < end) {
                                    if (index >= tokenCount) {
                                        Log.e("==err==", l);
                                        break;
                                    }
                                    tokens[index] = l.substring(start, end);
                                }
                            }
                        }

                        for (int k = 0; k < tokenCount - index - 1; k++) {
                            tokens[k + index + 1] = "";
                        }

                        datas.add(new PetMountData(tokens[0], tokens[1], tokens[2], tokens[3], tokens[4], tokens[6]));
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

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.row_grid_item, parent, false);

            return new MyViewHolder(v);
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            holder.itemView.setTag(position);
            Glide.with(findViewById(R.id.list_view)).load("https://tools.ffxiv.cn/lajipai//image/chongwuzuoqi-ui/"
                    + datas.get(position).id + ".png")
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
                Intent i = new Intent(PetsActivity.this, PetMountDetailActivity.class);
                i.putExtra(PetMountDetailActivity.KEY_PET, datas.get(position));
                startActivity(i);
            });
        }
    }
}
