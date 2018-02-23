package com.xdluoyang.ffxivtools.model;

//编号,音乐,位置,坐标,NPC,其他,备注,类型,BGM
public class MusicData {
    public int num;
    public String name;
    public String pos;
    public String posXy;
    public String npc;
    public String others;
    public String comment;
    public int type;
    public String musicId;

    public MusicData(int num, String name, String pos, String posXy, String npc, String others, String comment, int type, String musicId) {
        this.num = num;
        this.name = name;
        this.pos = pos;
        this.posXy = posXy;
        this.npc = npc;
        this.others = others;
        this.comment = comment;
        this.type = type;
        this.musicId = musicId;
    }
}
