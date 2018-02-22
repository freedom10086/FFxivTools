package com.xdluoyang.ffxivtools.model;

import android.os.Parcel;
import android.os.Parcelable;


//pets 大图编码,小图编码,版本,名称,中国区获得方法,国际服获得方法,说明
//mounts 大图编码,小图编码,版本,飞行,名称,中国区获得方法,国际服获得方法,说明
public class PetMountData implements Parcelable {
    public String bigId;
    public String id;
    public String version;
    public String name;
    public String method;
    public String description;
    public boolean fly;

    public PetMountData(String bigId, String id, String version, String name, String method, String description) {
        this.bigId = bigId;
        this.id = id;
        this.version = version;
        this.name = name;
        this.method = method;
        this.description = description;
    }

    protected PetMountData(Parcel in) {
        bigId = in.readString();
        id = in.readString();
        version = in.readString();
        name = in.readString();
        method = in.readString();
        description = in.readString();
        fly = (in.readInt() == 1);
    }

    public static final Creator<PetMountData> CREATOR = new Creator<PetMountData>() {
        @Override
        public PetMountData createFromParcel(Parcel in) {

            return new PetMountData(in);
        }

        @Override
        public PetMountData[] newArray(int size) {
            return new PetMountData[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(bigId);
        parcel.writeString(id);
        parcel.writeString(version);
        parcel.writeString(name);
        parcel.writeString(method);
        parcel.writeString(description);
        parcel.writeInt(fly ? 1 : 0);
    }
}
