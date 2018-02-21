package com.xdluoyang.ffxivtools.huntapi;

import android.content.Context;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Admin on 2018/2/20.
 */

public class Client {
    private static final String API_BASE = "https://api.ffxiv.cn/";
    private static Retrofit retrofit;
    private static Context context;

    public static void init(Context context){
        Client.context = context;
    }

    public static Service Instance() {
        if (retrofit == null) {
            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            OkHttpClient client = builder.build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(API_BASE)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }

        return retrofit.create(Service.class);
    }
}
