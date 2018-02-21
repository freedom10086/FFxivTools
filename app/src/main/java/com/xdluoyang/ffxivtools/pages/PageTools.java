package com.xdluoyang.ffxivtools.pages;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.xdluoyang.ffxivtools.R;

public class PageTools extends LazyPage {

    public static final int[] icons = new int[]{
            R.mipmap.pets, R.mipmap.mounts, R.mipmap.explore, R.mipmap.music, R.mipmap.dungeons, R.mipmap.aether,
            R.mipmap.jobtask, R.mipmap.hunt, R.mipmap.hunt, R.mipmap.dig};

    public static final String[] titles = new String[]{
            "宠物一览", "坐骑一览", "探索笔记", "乐谱一览", "副本一览", "风脉泉一览",
            "职业任务一览", "狩猎任务一览", "狩猎热点图", "藏宝图一览"};

    public static final Class<?>[] cls = new Class<?>[]{
            null,null,null,null,null,null,
            null,null,PageHuntHotMap.class,TestActivity.class};

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.page_tools, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.list_view);
        recyclerView.setAdapter(new MyAdapter());
        RecyclerView.LayoutManager manager = new GridLayoutManager(getActivity(), 2);
        recyclerView.setLayoutManager(manager);

        return view;
    }

    @Override
    public void onVisible(boolean isFirst) {
        Log.v("======", "PageTools onVisible:" + isFirst);
    }

    @Override
    public void onInVisible() {
        Log.v("======", "PageTools onInVisible");
    }

    private class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.row_tools_full, parent, false);

            return new MyViewHolder(v);
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            holder.itemView.setTag(position);
            holder.icon.setImageResource(icons[position]);
            if (holder.title != null) {
                holder.title.setText(titles[position]);
            }
        }

        @Override
        public int getItemCount() {
            return icons.length;
        }
    }

    private class MyViewHolder extends RecyclerView.ViewHolder {
        final ImageView icon;
        final TextView title;

        public MyViewHolder(final View itemView) {
            super(itemView);

            icon = itemView.findViewById(R.id.icon);
            title = itemView.findViewById(R.id.title);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = (int) itemView.getTag();
                    if (cls[position] != null)
                        switchActivity(cls[position]);
                }
            });


        }
    }
}
