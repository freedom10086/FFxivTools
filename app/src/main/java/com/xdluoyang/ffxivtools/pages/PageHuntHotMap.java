package com.xdluoyang.ffxivtools.pages;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.xdluoyang.ffxivtools.R;
import com.xdluoyang.ffxivtools.huntapi.Client;
import com.xdluoyang.ffxivtools.huntapi.HuntItem;
import com.xdluoyang.ffxivtools.huntapi.MapItem;
import com.xdluoyang.ffxivtools.widget.HotMapView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class PageHuntHotMap extends BaseActivity {

    private List<MapItem> datas = new ArrayList<>();
    private MyAdapter adapter;
    private HotMapView mapView;
    private SeekBar seekBar;

    private Location[] loc2 = new Location[]{
            new Location(134, "中拉诺西亚", "MiddleLaNoscea"),
            new Location(135, "拉诺西亚低地", "LowerLaNoscea"),
            new Location(137, "东拉诺西亚", "EasternLaNoscea"),
            new Location(138, "西拉诺西亚", "WesternLaNoscea"),
            new Location(139, "拉诺西亚高地", "UpperLaNoscea"),
            new Location(180, "拉诺西亚外地", "OuterLaNoscea"),
            new Location(140, "西萨纳兰", "WesternThanalan"),
            new Location(141, "中萨纳兰", "CentralThanalan"),
            new Location(145, "东萨纳兰", "EasternThanalan"),
            new Location(146, "南萨纳兰", "SouthernThanalan"),
            new Location(147, "北萨纳兰", "NorthernThanalan"),
            new Location(148, "黑衣森林中央林区", "CentralShroud"),
            new Location(152, "黑衣森林东部林区", "EastShroud"),
            new Location(153, "黑衣森林南部林区", "SouthShroud"),
            new Location(154, "黑衣森林北部林区", "NorthShroud"),
            new Location(155, "库尔札斯中央高地", "CoerthasCentralHighlands"),
            new Location(156, "摩杜纳", "MorDhona"),
    };
    private Location[] loc3 = new Location[]{
            new Location(397, "库尔札斯西部高地", "CoerthasWesternHighlands"),
            new Location(398, "龙堡参天高地", "TheDravanianForelands"),
            new Location(399, "龙堡内陆低地", "TheDravanianHinterlands"),
            new Location(400, "翻云雾海", "TheChurningMists"),
            new Location(401, "阿巴拉提亚云海", "TheSeaofClouds"),
            new Location(402, "魔大陆阿济兹拉", "AzysLla"),};
    private Location[] loc4 = new Location[]{
            new Location(612, "基拉巴尼亚边区", "TheFringes"),
            new Location(613, "红玉海", "TheRubySea"),
            new Location(614, "延夏", "Yanxia"),
            new Location(620, "基拉巴尼亚山区", "ThePeaks"),
            new Location(621, "基拉巴尼亚湖区", "TheLochs"),
            new Location(622, "太阳神草原", "TheAzimSteppe"),};

    class Location {
        int id;
        String nameCN;
        String nameEN;

        public Location(int id, String nameCN, String nameEN) {
            this.id = id;
            this.nameCN = nameCN;
            this.nameEN = nameEN;
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setToolBar(true, "狩猎热点图");
        setContentView(R.layout.activity_hunt_hot_map);

        RecyclerView recyclerView = findViewById(R.id.list_view);
        adapter = new MyAdapter();
        recyclerView.setAdapter(adapter);
        RecyclerView.LayoutManager manager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);

        mapView = findViewById(R.id.hot_map_view);
        seekBar = findViewById(R.id.filter_seek);

        seekBar.setVisibility(View.GONE);
        seekBar.setEnabled(false);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (currentSpecs != null) {
                    int min = currentSpecs[0];
                    int max = currentSpecs[1];

                    mapView.setThreshold((int) (progress * 1.0f / 100 * (max - min)));
                }

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        loadData();
    }


    private void loadData() {
        seekBar.setEnabled(false);
        seekBar.setVisibility(View.GONE);
        seekBar.setProgress(0);

        Client.Instance().getHuntMapList().enqueue(new Callback<List<MapItem>>() {
            @Override
            public void onResponse(Call<List<MapItem>> call, retrofit2.Response<List<MapItem>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    datas = response.body();
                    Collections.sort(datas, (t0, t1) -> {
                        if (t0.MapID > t1.MapID)
                            return 1;
                        else if (t0.MapID < t1.MapID)
                            return -1;
                        else {
                            if (Objects.equals(t0.Type, "S")) return -1;
                            if (Objects.equals(t0.Type, "A") && Objects.equals(t1.Type, "B"))
                                return -1;
                            return 1;
                        }
                    });

                    adapter.notifyDataSetChanged();
                }

            }

            @Override
            public void onFailure(Call<List<MapItem>> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    private int[] currentSpecs = null;

    private void loadDetail(String name) {
        Client.Instance().getHuntDetail(name).enqueue(new Callback<List<HuntItem>>() {
            @Override
            public void onResponse(Call<List<HuntItem>> call, Response<List<HuntItem>> response) {
                if (response.isSuccessful() && response.body() != null) {

                    List<HuntItem> data = response.body();

                    // min max avg
                    int[] spec = getMapDataSpec(data);
                    currentSpecs = spec;

                    // default progress is 0.4 * avg
                    float times = 0.4f;
                    int progress = (spec[0] == spec[1]) ? 100 : (int) ((spec[2] - spec[0]) * 100 / (spec[1] - spec[0]) * times);
                    seekBar.setProgress(progress);
                    seekBar.setVisibility(View.VISIBLE);
                    seekBar.setEnabled(true);

                    mapView.setData(response.body(), (int) (spec[2] * times));
                }
            }

            @Override
            public void onFailure(Call<List<HuntItem>> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    public int[] getMapDataSpec(List<HuntItem> data) {
        int min = Integer.MAX_VALUE;
        int max = 0;
        int sum = 0;

        for (HuntItem item : data) {
            if (item.Counts < min) {
                min = item.Counts;
            }

            if (item.Counts > max) {
                max = item.Counts;
            }

            sum += item.Counts;
        }
        int avg = sum / data.size();
        return new int[]{min, max, avg};
    }

    private class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.row_hunt_item, parent, false);

            return new MyViewHolder(v);
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            holder.itemView.setTag(position);
            MapItem item = datas.get(position);

            holder.name.setText(item.MapName + "-" + item.Name + "(" + item.Type + ")");
        }

        @Override
        public int getItemCount() {
            return datas.size();
        }
    }

    private class MyViewHolder extends RecyclerView.ViewHolder {
        final TextView name;

        public MyViewHolder(final View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.name);

            itemView.setOnClickListener(view -> {
                int position = (int) itemView.getTag();
                loadDetail(datas.get(position).Name);
                int mapId = datas.get(position).MapID;
                String mapName = getMapName(mapId);

                mapView.setData(null);
                if (mapName != null)
                    Glide.with(mapView)
                            .load("https://hunt.ffxiv.xin/img/map/500/" + mapName + ".png")
                            .into(mapView);
            });
        }
    }

    private String getMapName(int mapId) {
        for (int i = 0; i < loc2.length; i++) {
            if (loc2[i].id == mapId) {
                return loc2[i].nameEN;
            }
        }

        for (int i = 0; i < loc3.length; i++) {
            if (loc3[i].id == mapId) {
                return loc3[i].nameEN;
            }
        }

        for (int i = 0; i < loc4.length; i++) {
            if (loc4[i].id == mapId) {
                return loc4[i].nameEN;
            }
        }

        return null;
    }
}
