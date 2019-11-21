package com.xdluoyang.ffxivtools.model;

import android.os.Parcel;
import android.os.Parcelable;

//编码,顺序,副本名称,任务,位置,NPC,地图map,装等限制,等级,所属,类型
public class DungeonData implements Parcelable {
    public String id;
    public int sort;
    public String name;
    public String renwu;
    public String pos;
    public String npc;
    public String mapPos; // id=20&x=12.0&y=14.3
    public String zhuandeng;
    public String level;
    public int belong;
    public int type; //1-10

    public DungeonData(String id, int sort, String name,
                       String renwu, String pos, String mapPos, String npc,
                       String zhuandeng, String level, int belong, int type) {
        this.id = id;
        this.sort = sort;
        this.name = name;
        this.renwu = renwu;
        this.pos = pos;
        this.mapPos = mapPos;
        this.npc = npc;
        this.zhuandeng = zhuandeng;
        this.level = level;
        this.belong = belong;
        this.type = type;
    }

    private DungeonData(Parcel in) {
        id = in.readString();
        sort = in.readInt();
        name = in.readString();
        renwu = in.readString();
        pos = in.readString();
        mapPos = in.readString();
        npc = in.readString();
        zhuandeng = in.readString();
        level = in.readString();
        belong = in.readInt();
        type = in.readInt();
    }

    public static final Creator<DungeonData> CREATOR = new Creator<DungeonData>() {
        @Override
        public DungeonData createFromParcel(Parcel in) {
            return new DungeonData(in);
        }

        @Override
        public DungeonData[] newArray(int size) {
            return new DungeonData[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeInt(sort);
        parcel.writeString(name);
        parcel.writeString(renwu);
        parcel.writeString(pos);
        parcel.writeString(mapPos);
        parcel.writeString(npc);
        parcel.writeString(zhuandeng);
        parcel.writeString(level);
        parcel.writeInt(belong);
        parcel.writeInt(type);
    }
}
