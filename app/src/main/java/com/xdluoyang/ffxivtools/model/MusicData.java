package com.xdluoyang.ffxivtools.model;

//乐谱名（日）,乐谱名（中）,获得方法,编号,类型,网易云,网易云类型（0无、2音乐、3电台）
public class MusicData {
    public int num;
    public String name;
    public String method;
    public int type;
    public String musicId;
    public String musicType;

    public MusicData(int num, String name, String method, int type, String musicId, String musicType) {
        this.num = num;
        this.name = name;
        this.method = method;
        this.type = type;
        this.musicId = musicId;
        this.musicType = musicType;
    }
}
