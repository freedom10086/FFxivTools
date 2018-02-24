package com.xdluoyang.ffxivtools.model;

import android.os.Parcel;
import android.os.Parcelable;

//编码,顺序,所属,副本名称,任务,位置,坐标,NPC,装等限制,类型,是否主线
public class DungeonData implements Parcelable {
    public String id;
    public int sort;
    public String name;

    public String renwu;
    public String pos;
    public String posXy;
    public String npc;

    public String zhuandeng;
    public int type; //1-10
    public boolean isMainLine;

    public DungeonData(String id, int sort, String name,
                       String renwu, String pos, String posXy, String npc,
                       String zhuandeng, int type, boolean isMainLine) {
        this.id = id;
        this.sort = sort;
        this.name = name;
        this.renwu = renwu;
        this.pos = pos;
        this.posXy = posXy;
        this.npc = npc;
        this.zhuandeng = zhuandeng;
        this.type = type;
        this.isMainLine = isMainLine;
    }

    private DungeonData(Parcel in) {
        id = in.readString();
        sort = in.readInt();
        name = in.readString();
        renwu = in.readString();
        pos = in.readString();
        posXy = in.readString();
        npc = in.readString();
        zhuandeng = in.readString();
        type = in.readInt();
        isMainLine = in.readInt() != 0;
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
        parcel.writeString(posXy);
        parcel.writeString(npc);
        parcel.writeString(zhuandeng);
        parcel.writeInt(type);
        parcel.writeInt(isMainLine ? 1 : 0);
    }
}
