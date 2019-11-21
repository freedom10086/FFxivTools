package com.xdluoyang.ffxivtools.model;


import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

// mapid name [] * 14 pos ((16.2　14.2)Z:0.6)
public class WindData implements Parcelable {

    public String mapId;

    public String name;

    public List<String> posList;

    public WindData(String mapId, String name, List<String> posList) {
        this.mapId = mapId;
        this.name = name;
        this.posList = posList;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mapId);
        dest.writeString(this.name);
        dest.writeStringList(this.posList);
    }

    protected WindData(Parcel in) {
        this.mapId = in.readString();
        this.name = in.readString();
        this.posList = in.createStringArrayList();
    }

    public static final Parcelable.Creator<WindData> CREATOR = new Parcelable.Creator<WindData>() {
        @Override
        public WindData createFromParcel(Parcel source) {
            return new WindData(source);
        }

        @Override
        public WindData[] newArray(int size) {
            return new WindData[size];
        }
    };
}
