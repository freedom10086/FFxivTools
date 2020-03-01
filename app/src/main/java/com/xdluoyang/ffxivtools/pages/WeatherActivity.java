package com.xdluoyang.ffxivtools.pages;

import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

import com.xdluoyang.ffxivtools.R;
import com.xdluoyang.ffxivtools.util.TimeUtil;

import java.util.Timer;
import java.util.TimerTask;

public class WeatherActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        Handler mainHandler = new Handler(getMainLooper());

        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            synchronized public void run() {
                mainHandler.post(() -> {
                    updateEorezeaTime();
                });
            }

        }, 100, (long) TimeUtil.EORZEA_TIME_MINUTE_MILLS);
    }

    private void updateEorezeaTime() {
        TextView eorezeaTimeText = findViewById(R.id.tv_eorezea_time);
        long current = System.currentTimeMillis();
        TimeUtil.EorezeaTime eorezeaTime = TimeUtil.toEorezeaTime(current);

        eorezeaTimeText.setText(eorezeaTime.getSimpleTimeString());
    }
}
