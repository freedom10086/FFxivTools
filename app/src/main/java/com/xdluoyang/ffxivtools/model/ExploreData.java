package com.xdluoyang.ffxivtools.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Objects;


//小图代码,大图代码,顺序,名称,版本,地点,X,Y,天气,天气图片,时间,动作,动作代码,动作图片,备注,位置图片1,位置图片2
public class ExploreData implements Parcelable {
    public String bigId;
    public String id;
    public String num;
    public String name;
    public int version; //0 1 2 新生篇 苍天篇 红莲篇
    public String map; //地点
    public String x;
    public String y;
    public String wether; //?
    public String wetherImage;// ?
    public String time;// ?
    public String action; //动作,动作代码
    public String actionCode;
    public String actionImage;
    public String comment;
    public String image1;
    public String image2;

    public ExploreData(String id, String bigId, String num, String name, String version, String map,
                       String x, String y, String wether, String wetherImage, String time,
                       String action, String actionCode, String actionImage, String comment,
                       String image1, String image2) {
        this.bigId = bigId;
        this.id = id;
        this.num = num;
        this.name = name;
        this.version = Objects.equals(version, "新生篇") ?
                0 : (Objects.equals(version, "苍天篇") ? 1 : 2);
        this.map = map;
        this.x = x;
        this.y = y;
        this.wether = wether;
        this.wetherImage = wetherImage;
        this.time = time;
        this.action = action;
        this.actionCode = actionCode;
        this.actionImage = actionImage;
        this.comment = comment;
        this.image1 = image1;
        this.image2 = image2;
    }

    protected ExploreData(Parcel in) {
        bigId = in.readString();
        id = in.readString();
        num = in.readString();
        name = in.readString();
        version = in.readInt();
        map = in.readString();
        x = in.readString();
        y = in.readString();
        wether = in.readString();
        wetherImage = in.readString();
        time = in.readString();
        action = in.readString();
        actionCode = in.readString();
        actionImage = in.readString();
        comment = in.readString();
        image1 = in.readString();
        image2 = in.readString();
    }

    public static final Creator<ExploreData> CREATOR = new Creator<ExploreData>() {
        @Override
        public ExploreData createFromParcel(Parcel in) {
            return new ExploreData(in);
        }

        @Override
        public ExploreData[] newArray(int size) {
            return new ExploreData[size];
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
        parcel.writeString(num);
        parcel.writeString(name);
        parcel.writeInt(version);
        parcel.writeString(map);
        parcel.writeString(x);
        parcel.writeString(y);
        parcel.writeString(wether);
        parcel.writeString(wetherImage);
        parcel.writeString(time);
        parcel.writeString(action);
        parcel.writeString(actionCode);
        parcel.writeString(actionImage);
        parcel.writeString(comment);
        parcel.writeString(image1);
        parcel.writeString(image2);
    }
}
