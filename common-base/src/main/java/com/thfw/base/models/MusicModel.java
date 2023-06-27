package com.thfw.base.models;

import android.text.TextUtils;

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

    @SerializedName("AARTIST")
    public String aartist;
    @SerializedName("ALBUM")
    public String album;
    @SerializedName("ALBUMID")
    public String albumid;
    @SerializedName("ARTIST")
    public String artist;
    @SerializedName("ARTISTID")
    public String artistid;
    @SerializedName("COPYRIGHT")
    public String copyright;
    @SerializedName("CanSetRing")
    public String CanSetRing;
    @SerializedName("CanSetRingback")
    public String CanSetRingback;
    @SerializedName("DC_TARGETID")
    public String dcTargetid;
    @SerializedName("DC_TARGETTYPE")
    public String dcTargettype;
    @SerializedName("DURATION")
    public String duration;
    @SerializedName("FORMATS")
    public String formats;
    @SerializedName("HASECHO")
    public String hasecho;
    @SerializedName("IS_POINT")
    public String isPoint;
    @SerializedName("MKVRID")
    public String mkvrid;
    @SerializedName("MP3NSIG1")
    public String MP3NSIG1;
    @SerializedName("MP3NSIG2")
    public String MP3NSIG2;
    @SerializedName("MP3RID")
    public String MP3RID;
    @SerializedName("MUSICRID")
    public String musicrid;
    @SerializedName("MUTI_VER")
    public String mutiVer;
    @SerializedName("MVFLAG")
    public String mvflag;
    @SerializedName("MVPIC")
    public String mvpic;
    @SerializedName("NAME")
    public String name; // 歌名
    @SerializedName("NEW")
    public String newX;
    @SerializedName("NSIG1")
    public String NSIG1;
    @SerializedName("NSIG2")
    public String NSIG2;
    @SerializedName("ONLINE")
    public String online;
    @SerializedName("PAY")
    public String pay;
    @SerializedName("PICPATH")
    public String picpath;
    @SerializedName("PLAYCNT")
    public String playcnt;
    @SerializedName("SCORE100")
    public String SCORE100;
    @SerializedName("SIG1")
    public String SIG1;
    @SerializedName("SIG2")
    public String SIG2;
    @SerializedName("SONGNAME")
    public String songname;
    @SerializedName("SUBTITLE")
    public String subtitle;
    @SerializedName("TAG")
    public String tag;
    @SerializedName("ad_subtype")
    public String adSubtype;
    @SerializedName("ad_type")
    public String adType;
    @SerializedName("allartistid")
    public String allartistid;
    @SerializedName("audiobookpayinfo")
    public AudiobookpayinfoBean audiobookpayinfo;
    @SerializedName("barrage")
    public String barrage;
    @SerializedName("cache_status")
    public String cacheStatus;
    @SerializedName("content_type")
    public String contentType;
    @SerializedName("fpay")
    public String fpay;
    @SerializedName("hts_MVPIC")
    public String htsMVPIC;
    @SerializedName("info")
    public String info;
    @SerializedName("iot_info")
    public String iotInfo;
    @SerializedName("isdownload")
    public String isdownload;
    @SerializedName("isshowtype")
    public String isshowtype;
    @SerializedName("isstar")
    public String isstar;
    @SerializedName("mp4sig1")
    public String mp4sig1;
    @SerializedName("mp4sig2")
    public String mp4sig2;
    @SerializedName("mvpayinfo")
    public MvpayinfoBean mvpayinfo;
    @SerializedName("originalsongtype")
    public String originalsongtype;
    @SerializedName("payInfo")
    public PayInfoBean payInfo;
    @SerializedName("spPrivilege")
    public String spPrivilege;
    @SerializedName("subsStrategy")
    public String subsStrategy;
    @SerializedName("subsText")
    public String subsText;
    @SerializedName("terminal")
    public String terminal;
    @SerializedName("tme_musician_adtype")
    public String tmeMusicianAdtype;
    @SerializedName("tpay")
    public String tpay;
    @SerializedName("web_albumpic_short")
    public String webAlbumpicShort;
    @SerializedName("web_artistpic_short")
    public String webArtistpicShort;
    @SerializedName("web_timingonline")
    public String webTimingonline;
    @SerializedName("SUBLIST")
    public List<?> sublist;

    public static class AudiobookpayinfoBean implements Serializable {
        @SerializedName("download")
        public String download;
        @SerializedName("play")
        public String play;
    }

    public static class MvpayinfoBean implements Serializable {
        @SerializedName("download")
        public String download;
        @SerializedName("play")
        public String play;
        @SerializedName("vid")
        public String vid;
    }

    public static class PayInfoBean implements Serializable {
        @SerializedName("cannotDownload")
        public String cannotDownload;
        @SerializedName("cannotOnlinePlay")
        public String cannotOnlinePlay;
        @SerializedName("download")
        public String download;
        @SerializedName("feeType")
        public FeeTypeBean feeType;
        @SerializedName("limitfree")
        public String limitfree;
        @SerializedName("listen_fragment")
        public String listenFragment;
        @SerializedName("local_encrypt")
        public String localEncrypt;
        @SerializedName("ndown")
        public String ndown;
        @SerializedName("nplay")
        public String nplay;
        @SerializedName("overseas_ndown")
        public String overseasNdown;
        @SerializedName("overseas_nplay")
        public String overseasNplay;
        @SerializedName("play")
        public String play;
        @SerializedName("refrain_end")
        public String refrainEnd;
        @SerializedName("refrain_start")
        public String refrainStart;
        @SerializedName("tips_intercept")
        public String tipsIntercept;

        public static class FeeTypeBean implements Serializable {
            @SerializedName("album")
            public String album;
            @SerializedName("bookvip")
            public String bookvip;
            @SerializedName("song")
            public String song;
            @SerializedName("vip")
            public String vip;
        }
    }


    public String getMp3Url() {
        // https://apis.jxcxin.cn/doc/kuwo.html
        return "https://apis.jxcxin.cn/api/kuwo?apiKey=4fd17e5b3769e9888c734229f5790cdc&id=" + dcTargetid + "&type=mp3";
    }

    public Object getPicUrl() {
        return R.drawable.ic_music_album;
    }

    public int getId() {
        return Integer.parseInt(dcTargetid);
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
        if (!TextUtils.isEmpty(webArtistpicShort)) {
            return "http://img4.kuwo.cn/star/starheads/" + webArtistpicShort;
        } else if (!TextUtils.isEmpty(htsMVPIC)) {
            return htsMVPIC;
        } else if (!TextUtils.isEmpty(webAlbumpicShort)) {
            return " https://img4.kuwo.cn/star/albumcover/" + webArtistpicShort;
        }
        return "";
    }

    public String getLyricId() {
        return "https://apis.jxcxin.cn/api/kuwo?apiKey=4fd17e5b3769e9888c734229f5790cdc&id=" + dcTargetid + "&type=lrc";
    }

    public AudioEtcDetailModel.AudioItemModel toAudioItemModel() {
        AudioEtcDetailModel.AudioItemModel audioItemModel = new AudioEtcDetailModel.AudioItemModel();
        audioItemModel.setId(getId());
        audioItemModel.setMusicId(getId());
        audioItemModel.setSfile(getMp3Url());
        audioItemModel.setImg(getPicUrl());
        audioItemModel.setMp3PicId(getPicId());
        audioItemModel.setLrcUrl(getLyricId());
        if (TextUtils.isEmpty(album)) {
            audioItemModel.setTitle(getName() + " - " + artist);
        } else {
            audioItemModel.setTitle(getName() + " - " + artist + " • 《" + album + "》");
        }
        audioItemModel.setTitle(audioItemModel.getTitle().replaceAll("&apos;", "'"));
        audioItemModel.setTitle(audioItemModel.getTitle().replaceAll("( |&nbsp;)", " "));
        audioItemModel.setAuthor(getAlbum());
        audioItemModel.setMp3(true);
        audioItemModel.setAutoFinished(true);
        return audioItemModel;
    }

    public static class MusicResult {
        public List<MusicModel> abslist;
    }


}
