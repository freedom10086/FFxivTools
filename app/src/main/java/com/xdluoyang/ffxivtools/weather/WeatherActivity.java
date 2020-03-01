package com.xdluoyang.ffxivtools.weather;

import android.os.Bundle;
import android.os.Handler;
import android.util.Pair;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.xdluoyang.ffxivtools.R;
import com.xdluoyang.ffxivtools.pages.BaseActivity;
import com.xdluoyang.ffxivtools.util.TimeUtil;
import com.xdluoyang.ffxivtools.util.Util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class WeatherActivity extends BaseActivity {

    private List<Pair<String, String>> weatherNames = new ArrayList<>();

    private List<LocationWeatherItem> zones;

    Map<String, List<WeatherItem>> weatherRatesMap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        try {
            String weatherNameJson = Util.readStreamString(getAssets().open("weather_name.json"));
            Map<Object, Object> weatherNameMap = JSON.parseObject(weatherNameJson, Map.class);
            List<String> cnNames = (List<String>) weatherNameMap.get("cn_name");
            List<String> enNames = (List<String>) weatherNameMap.get("en_name");

            for (int i = 0; i < enNames.size(); i++) {
                weatherNames.add(Pair.create(enNames.get(i), cnNames.get(i)));
            }

            String zoneIndexJson = Util.readStreamString(getAssets().open("weather_zone_index.json"));
            zones = JSON.parseObject(zoneIndexJson, new TypeReference<List<LocationWeatherItem>>() {
            });

            String waetherJson = Util.readStreamString(getAssets().open("weather_rate.json"));
            weatherRatesMap = JSON.parseObject(waetherJson, new TypeReference<Map<String, List<WeatherItem>>>() {
            });

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            finish();
            return;
        }


        Handler mainHandler = new Handler(getMainLooper());
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            synchronized public void run() {
                mainHandler.post(() -> {
                    updateEorezeaTime();
                });
            }

        }, 100, (long) TimeUtil.EORZEA_TIME_MINUTE_MILLS);

        StringBuilder weatherText = new StringBuilder();
        long time = System.currentTimeMillis();
        for (LocationWeatherItem zone : zones) {
            weatherText.append("--------");
            weatherText.append("\n");
            for (LocationWeatherItem subZone : zone.getZones()) {
                int rate = calculateForecastTarget(time);
                List<WeatherItem> weatherItems = weatherRatesMap.get(String.valueOf(subZone.getWeatherRate()));
                for (WeatherItem w: weatherItems) {
                    if (rate < w.getRate()) {
                        weatherText.append(subZone.getName() + ": " +  weatherNames.get(w.getWeather()).second);
                        weatherText.append("\n");
                        break;
                    }
                }

            }
        }

        TextView weatherInfoTextView = findViewById(R.id.tv_weather_info);
        weatherInfoTextView.setText(weatherText.toString());
    }

    private void updateEorezeaTime() {
        TextView eorezeaTimeText = findViewById(R.id.tv_eorezea_time);
        long current = System.currentTimeMillis();
        TimeUtil.EorezeaTime eorezeaTime = TimeUtil.toEorezeaTime(current);

        eorezeaTimeText.setText(eorezeaTime.getSimpleTimeString());
    }

    private int calculateForecastTarget(long timemill) {
        // Thanks to Rogueadyn's SaintCoinach library for this calculation.
        int unixSeconds = (int) (timemill / 1000);
        // Get Eorzea hour for weather start
        int bell = unixSeconds / 175;
        // Do the magic 'cause for calculations 16:00 is 0, 00:00 is 8 and 08:00 is 16
        int increment = (bell + 8 - (bell % 8)) % 24;
        // Take Eorzea days since unix epoch
        int totalDays = unixSeconds / 4200;
        // 0x64 = 100
        int calcBase = totalDays * 100 + increment;
        // 0xB = 11
        int step1 = ((calcBase << 11) ^ calcBase); // uint
        int step2 = ((step1 >>> 8) ^ step1); // uint
        // 0x64 = 100
        return step2 % 100;
    }
}
