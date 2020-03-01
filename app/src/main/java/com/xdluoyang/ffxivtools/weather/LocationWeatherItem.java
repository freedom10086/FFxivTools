package com.xdluoyang.ffxivtools.weather;

import java.util.List;

public class LocationWeatherItem {

    private Integer id;

    private String name;

    private Integer weatherRate;

    private List<LocationWeatherItem> zones;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getWeatherRate() {
        return weatherRate;
    }

    public void setWeatherRate(Integer weatherRate) {
        this.weatherRate = weatherRate;
    }

    public List<LocationWeatherItem> getZones() {
        return zones;
    }

    public void setZones(List<LocationWeatherItem> zones) {
        this.zones = zones;
    }
}
