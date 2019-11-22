package com.xdluoyang.ffxivtools.pages;

import android.os.Bundle;
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

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.stfalcon.imageviewer.StfalconImageViewer;
import com.xdluoyang.ffxivtools.R;
import com.xdluoyang.ffxivtools.model.WindData;
import com.xdluoyang.ffxivtools.util.Util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;


public class WindDetailActivity extends BaseActivity {

    public static final String KEY_WIND = "key_wind";

    private RecyclerView recyclerView;

    private MyAdapter adapter;

    private WindData windData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wind_detail);

        windData = getIntent().getParcelableExtra(KEY_WIND);
        if (windData == null) {
            return;
        }

        setToolBar(true, windData.name);
        recyclerView = findViewById(R.id.list_view);

        int width = (int) Util.getScreenWidthDP(this);
        adapter = new MyAdapter();
        recyclerView.setAdapter(adapter);
        GridLayoutManager manager = new GridLayoutManager(this, width / 80);
        manager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (adapter.getItemViewType(position) == TYPE_SMALL_IMAGE) {
                    return 1;
                } else {
                    return width / 80;
                }
            }
        });
        recyclerView.setLayoutManager(manager);
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

    final Integer TYPE_BIG_IMAGE = 0;

    final Integer TYPE_QUEST_POS = 1;

    final Integer TYPE_SMALL_IMAGE = 2;

    private class MyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType == TYPE_QUEST_POS) {
                View v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.row_wind_detail_quest, parent, false);
                return new MyQuestHolder(v);
            } else {
                View v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.row_winds_detail_item, parent, false);
                return new MyViewHolder(v);
            }
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            holder.itemView.setTag(position);
            int viewType = getItemViewType(position);

            if (viewType == TYPE_BIG_IMAGE || viewType == TYPE_SMALL_IMAGE) {
                if (viewType == TYPE_SMALL_IMAGE) {
                    position = position - getQuestCount(windData) - 1;
                }
                String url = "https://tools.ffxiv.cn/lajipai/image/aether/" + windData.mapId
                        + (viewType == TYPE_BIG_IMAGE ? "" : String.valueOf(position + 1)) + ".jpeg";
                Glide.with(recyclerView)
                        .load(url)
                        .into(((MyViewHolder) holder).image);

                if (viewType == TYPE_BIG_IMAGE) {
                    ((MyViewHolder) holder).badge.setVisibility(View.GONE);
                } else {
                    ((MyViewHolder) holder).badge.setText(String.valueOf(position + 1));
                    ((MyViewHolder) holder).badge.setVisibility(View.VISIBLE);
                }
            } else {
                int index = position - 1;
                int curIndex = 0;
                String posText = "";
                for (int start = 10 ; start < 14; start ++) {
                    if (!TextUtils.isEmpty(windData.posList.get(start))) {
                        if (curIndex == index) {
                            posText = windData.posList.get(start);
                            break;
                        }
                        curIndex++;
                    }
                }
                ((MyQuestHolder) holder).pos.setText(posText);
            }
        }

        @Override
        public int getItemViewType(int position) {
            if (position == 0) {
                return TYPE_BIG_IMAGE;
            } else if (position < getQuestCount(windData) + 1) {
                return TYPE_QUEST_POS;
            }
            return TYPE_SMALL_IMAGE;
        }

        @Override
        public int getItemCount() {
            return 11 + getQuestCount(windData);
        }
    }

    private int getQuestCount(WindData windData) {
        int count = 0;
        for (int i = 10; i < 14; i++) {
            if (!TextUtils.isEmpty(windData.posList.get(i))) {
                count++;
            }
        }
        return count;
    }

    private class MyViewHolder extends RecyclerView.ViewHolder {
        final ImageView image;
        final TextView badge;

        public MyViewHolder(final View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id.image);
            badge = itemView.findViewById(R.id.num);

            itemView.setOnClickListener(view -> {
                int position = (int) itemView.getTag();
                if (position > 0) {
                    position = position - getQuestCount(windData);
                }
                List<String> images = new ArrayList<>(16);
                for (int i = 0; i <= 10; i++) {
                    String url = "https://tools.ffxiv.cn/lajipai/image/aether/" + windData.mapId
                            + (i == 0 ? "" : String.valueOf(i)) + ".jpeg";
                    images.add(url);
                }

                new StfalconImageViewer.Builder<>(recyclerView.getContext(), images,
                        (imageView, imageUrl) -> Glide.with(recyclerView).load(imageUrl).into(imageView))
                        .withTransitionFrom(image)
                        .withStartPosition(position)
                        .show(true);
            });
        }
    }

    private class MyQuestHolder extends RecyclerView.ViewHolder {
        final ImageView image;
        final TextView pos;

        public MyQuestHolder(final View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id.image);
            pos = itemView.findViewById(R.id.text);

            itemView.setOnClickListener(view -> {
                int position = (int) itemView.getTag();
                //pos.setText(windData.posList.get(position + 6 - 1));
            });
        }
    }
}
