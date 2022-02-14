package com.thfw.base.models;

import com.google.gson.annotations.SerializedName;
import com.thfw.base.base.IModel;

import java.io.Serializable;
import java.util.List;

/**
 * Author:pengs
 * Date: 2022/2/14 9:41
 * Describe:Todo
 */
public class TaskMusicEtcModel implements IModel {


    /**
     * collection_id : 632
     * music_list : [{"id":5149,"title":"重置大脑","sfile":"http://resource.soulbuddy.cn/public/uploads/music/2110271044436178bd1bed748.mp3","status":0},{"id":5150,"title":"迎接周末","sfile":"http://resource.soulbuddy.cn/public/uploads/music/2110271044316178bd0f2a682.mp3","status":0},{"id":5151,"title":"独处时间","sfile":"http://resource.soulbuddy.cn/public/uploads/music/2110271043056178bcb999be9.mp3","status":0}]
     */

    @SerializedName("collection_id")
    private int collectionId;
    @SerializedName("music_list")
    private List<MusicListBean> musicList;

    public int getCollectionId() {
        return collectionId;
    }

    public void setCollectionId(int collectionId) {
        this.collectionId = collectionId;
    }

    public List<MusicListBean> getMusicList() {
        return musicList;
    }

    public void setMusicList(List<MusicListBean> musicList) {
        this.musicList = musicList;
    }

    public static class MusicListBean implements Serializable {
        /**
         * id : 5149
         * title : 重置大脑
         * sfile : http://resource.soulbuddy.cn/public/uploads/music/2110271044436178bd1bed748.mp3
         * status : 0
         */

        @SerializedName("id")
        private int id;
        @SerializedName("title")
        private String title;
        @SerializedName("sfile")
        private String sfile;
        @SerializedName("status")
        private int status;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getSfile() {
            return sfile;
        }

        public void setSfile(String sfile) {
            this.sfile = sfile;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }
    }
}
