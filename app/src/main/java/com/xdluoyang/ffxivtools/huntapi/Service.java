package com.xdluoyang.ffxivtools.huntapi;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;


public interface Service {
    //查询地图列表
    @GET("ajax/hunt/Analysis/")
    Call<List<MapItem>> getHuntMapList();


    //查询某个怪物的分布
    @GET("ajax/hunt/Analysis/{name}")
    Call<List<HuntItem>> getHuntDetail(@Path("name") String name);
}
