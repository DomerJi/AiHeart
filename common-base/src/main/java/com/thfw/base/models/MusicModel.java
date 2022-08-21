package com.thfw.base.models;

import com.google.gson.annotations.SerializedName;
import com.thfw.base.R;

import java.io.Serializable;
import java.util.List;

/**
 * Author:pengs
 * Date: 2022/8/19 9:16
 * Describe:网易云音乐临时（待验证）
 */
public class MusicModel implements Serializable {


    /**
     * id : 500665346
     * name : 平凡之路 (Live)
     * artist : ["朴树"]
     * album : 乐人・Live：朴树“好好地II”巡回演唱会上海站(Live)
     * pic_id : 109951163009071893
     * url_id : 500665346
     * lyric_id : 500665346
     * source : netease
     */

    @SerializedName("id")
    private int id;
    @SerializedName("name")
    private String name;
    @SerializedName("album")
    private String album;
    @SerializedName("pic_id")
    private String picId;
    @SerializedName("url_id")
    private int urlId;
    @SerializedName("lyric_id")
    private int lyricId;
    @SerializedName("source")
    private String source;
    @SerializedName("artist")
    private List<String> artist;


    public String getMp3Url() {
        return String.format("http://music.163.com/song/media/outer/url?id=%d.mp3", urlId);
    }

    public Object getPicUrl() {
        return R.drawable.ic_music_album;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getPicId() {
        return picId;
    }

    public void setPicId(String picId) {
        this.picId = picId;
    }

    public int getUrlId() {
        return urlId;
    }

    public void setUrlId(int urlId) {
        this.urlId = urlId;
    }

    public int getLyricId() {
        return lyricId;
    }

    public void setLyricId(int lyricId) {
        this.lyricId = lyricId;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public List<String> getArtist() {
        return artist;
    }

    public void setArtist(List<String> artist) {
        this.artist = artist;
    }

    public AudioEtcDetailModel.AudioItemModel toAudioItemModel() {
        AudioEtcDetailModel.AudioItemModel audioItemModel = new AudioEtcDetailModel.AudioItemModel();
        audioItemModel.setId(getId());
        audioItemModel.setMusicId(getId());
        audioItemModel.setSfile(getMp3Url());
        audioItemModel.setImg(getPicUrl());
        audioItemModel.setTitle(getName() + " - " + getAlbum());
        audioItemModel.setAuthor(getAlbum());
        audioItemModel.setMp3(true);
        audioItemModel.setAutoFinished(true);
        return audioItemModel;
    }

    @Override
    public String toString() {
        return "MusicModel{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", album='" + album + '\'' +
                ", picId='" + picId + '\'' +
                ", urlId=" + urlId +
                ", lyricId=" + lyricId +
                ", source='" + source + '\'' +
                ", artist=" + artist +
                '}';
    }
}
