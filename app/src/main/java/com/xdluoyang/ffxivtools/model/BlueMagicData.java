package com.xdluoyang.ffxivtools.model;

import android.os.Parcel;
import android.os.Parcelable;

// 编号,小图标,大图标,名称,类型,属性,稀有度,获得方法,类别,技能效果,敌人等级
public class BlueMagicData implements Parcelable {

    public Integer num;

    public String imgSmall;

    public String imgBig;

    public String name;

    public String type;

    // 攻击属性
    public String attribute;

    public String xiYou;

    public String method;

    // 主要习得方法 类别
    public String methodType;

    public String effect;

    public String enemyLevel;

    public BlueMagicData(Integer num, String imgSmall, String imgBig, String name, String type,
                         String attribute, String xiYou, String method, String methodType, String effect, String enemyLevel) {
        this.num = num;
        this.imgSmall = imgSmall;
        this.imgBig = imgBig;
        this.name = name;
        this.type = type;
        this.attribute = attribute;
        this.xiYou = xiYou;
        this.method = method;
        this.methodType = methodType;
        this.effect = effect;
        this.enemyLevel = enemyLevel;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.num);
        dest.writeString(this.imgSmall);
        dest.writeString(this.imgBig);
        dest.writeString(this.name);
        dest.writeString(this.type);
        dest.writeString(this.attribute);
        dest.writeString(this.xiYou);
        dest.writeString(this.method);
        dest.writeString(this.methodType);
        dest.writeString(this.effect);
        dest.writeString(this.enemyLevel);
    }

    protected BlueMagicData(Parcel in) {
        this.num = (Integer) in.readValue(Integer.class.getClassLoader());
        this.imgSmall = in.readString();
        this.imgBig = in.readString();
        this.name = in.readString();
        this.type = in.readString();
        this.attribute = in.readString();
        this.xiYou = in.readString();
        this.method = in.readString();
        this.methodType = in.readString();
        this.effect = in.readString();
        this.enemyLevel = in.readString();
    }

    public static final Parcelable.Creator<BlueMagicData> CREATOR = new Parcelable.Creator<BlueMagicData>() {
        @Override
        public BlueMagicData createFromParcel(Parcel source) {
            return new BlueMagicData(source);
        }

        @Override
        public BlueMagicData[] newArray(int size) {
            return new BlueMagicData[size];
        }
    };
}
