package com.thfw.base.models;

import com.google.gson.annotations.SerializedName;
import com.thfw.base.base.IModel;

import java.io.Serializable;
import java.util.List;

/**
 * Author:pengs
 * Date: 2021/12/15 17:34
 * Describe:Todo
 */
public class AudioEtcDetailModel implements IModel {

    /**
     * collectionInfo : {"id":478,"intr":"排除杂念，找回优质睡眠","detail":"<p><br><\/p><p>睡眠是我们的精力之源，如何保证优质睡眠是当今社会一大难题。<\/p><p><br><\/p><p>很多时候，杂念太多，可能会让我们难以入睡，即便睡眠时间充足，第二天也会觉得毫无活力。<\/p><p><br><\/p><p>本系列将引导你如何识别睡意、排除杂念，进而提高夜晚的睡眠质量。<\/p><p><br><\/p><p><strong>适用人群：<\/strong><\/p><p>被失眠困扰的人<\/p><p>睡眠质量差的人<\/p><p><br><\/p>","num":8598,"img":"http://resource.soulbuddy.cn/public/uploads/musicPic/2104010748476065b31fe45ce.jpg","sort":520,"robot_id":7,"add_time":"2021-08-20 17:51:21","title":"14天改善睡眠","recommend_on":0,"up_shelf":1,"money":1,"root_id":8,"miniprogram_show_name":"14天改善睡眠","music_size":14,"listen_history_size":0,"collected":0}
     * musicList : [{"music_id":1828,"collection_id":8,"sort":0,"id":1828,"title":"改善睡眠 Day.1","sfile":"http://resource.soulbuddy.cn/public/uploads/music/2103310232146064176e13704.mp3","song":"","pic":null,"author":"","cat":0,"keywords":"","duration":"13:43","uid":0,"add_time":1627994013,"modify_time":1627994013,"delete_flag":1,"up_shelf":1,"robot_id":5,"label":null,"num":938},{"music_id":1829,"collection_id":8,"sort":0,"id":1829,"title":"改善睡眠 Day.2","sfile":"http://resource.soulbuddy.cn/public/uploads/music/210331023309606417a52b2f0.mp3","song":"","pic":null,"author":"","cat":0,"keywords":"","duration":"11:44","uid":0,"add_time":1627994013,"modify_time":1627994013,"delete_flag":1,"up_shelf":0,"robot_id":5,"label":null,"num":431},{"music_id":1830,"collection_id":8,"sort":0,"id":1830,"title":"改善睡眠 Day.3","sfile":"https://resource.soulbuddy.cn/public/uploads/music/210331023351606417cf5e401.mp3","song":"","pic":null,"author":"","cat":0,"keywords":"","duration":"13:27","uid":0,"add_time":1627994014,"modify_time":1627994014,"delete_flag":1,"up_shelf":0,"robot_id":5,"label":null,"num":336},{"music_id":1831,"collection_id":8,"sort":0,"id":1831,"title":"改善睡眠 Day.4","sfile":"https://resource.soulbuddy.cn/public/uploads/music/2103310240066064194682a8e.mp3","song":"","pic":null,"author":"","cat":0,"keywords":"","duration":"10:17","uid":0,"add_time":1627994014,"modify_time":1627994014,"delete_flag":1,"up_shelf":0,"robot_id":5,"label":null,"num":317},{"music_id":1832,"collection_id":8,"sort":0,"id":1832,"title":"改善睡眠 Day.5","sfile":"http://resource.soulbuddy.cn/public/uploads/music/210331024040606419680475a.mp3","song":"","pic":null,"author":"","cat":0,"keywords":"","duration":"12:42","uid":0,"add_time":1627994014,"modify_time":1627994014,"delete_flag":1,"up_shelf":0,"robot_id":5,"label":null,"num":318},{"music_id":1833,"collection_id":8,"sort":0,"id":1833,"title":"改善睡眠 Day.6","sfile":"https://resource.soulbuddy.cn/public/uploads/music/21033102440660641a361f665.mp3","song":"","pic":null,"author":"","cat":0,"keywords":"","duration":"11:27","uid":0,"add_time":1627994014,"modify_time":1627994014,"delete_flag":1,"up_shelf":0,"robot_id":5,"label":null,"num":277},{"music_id":1834,"collection_id":8,"sort":0,"id":1834,"title":"改善睡眠 Day.7","sfile":"https://resource.soulbuddy.cn/public/uploads/music/21033102444660641a5edfd94.mp3","song":"","pic":null,"author":"","cat":0,"keywords":"","duration":"13:39","uid":0,"add_time":1627994014,"modify_time":1627994014,"delete_flag":1,"up_shelf":0,"robot_id":5,"label":null,"num":288},{"music_id":1835,"collection_id":8,"sort":0,"id":1835,"title":"改善睡眠 Day.8","sfile":"https://resource.soulbuddy.cn/public/uploads/music/21060107113160b615e3cd08f.mp3","song":"","pic":null,"author":"","cat":0,"keywords":"","duration":"13:40","uid":0,"add_time":1627994014,"modify_time":1627994014,"delete_flag":1,"up_shelf":0,"robot_id":5,"label":null,"num":256},{"music_id":1836,"collection_id":8,"sort":0,"id":1836,"title":"改善睡眠 Day.9","sfile":"https://resource.soulbuddy.cn/public/uploads/music/21033102462260641abed870c.mp3","song":"","pic":null,"author":"","cat":0,"keywords":"","duration":"12:07","uid":0,"add_time":1627994014,"modify_time":1627994014,"delete_flag":1,"up_shelf":0,"robot_id":5,"label":null,"num":258},{"music_id":1837,"collection_id":8,"sort":0,"id":1837,"title":"改善睡眠 Day.10","sfile":"https://resource.soulbuddy.cn/public/uploads/music/21033102470460641ae8bb010.mp3","song":"","pic":null,"author":"","cat":0,"keywords":"","duration":"10:40","uid":0,"add_time":1627994014,"modify_time":1627994014,"delete_flag":1,"up_shelf":0,"robot_id":5,"label":null,"num":253},{"music_id":1838,"collection_id":8,"sort":0,"id":1838,"title":"改善睡眠 Day.11","sfile":"https://resource.soulbuddy.cn/public/uploads/music/21033102475660641b1cc1cdb.mp3","song":"","pic":null,"author":"","cat":0,"keywords":"","duration":"11:37","uid":0,"add_time":1627994014,"modify_time":1627994014,"delete_flag":1,"up_shelf":0,"robot_id":5,"label":null,"num":249},{"music_id":1839,"collection_id":8,"sort":0,"id":1839,"title":"改善睡眠 Day.12","sfile":"https://resource.soulbuddy.cn/public/uploads/music/21033102482960641b3d62235.mp3","song":"","pic":null,"author":"","cat":0,"keywords":"","duration":"10:32","uid":0,"add_time":1627994014,"modify_time":1627994014,"delete_flag":1,"up_shelf":0,"robot_id":5,"label":null,"num":231},{"music_id":1840,"collection_id":8,"sort":0,"id":1840,"title":"改善睡眠 Day.13","sfile":"https://resource.soulbuddy.cn/public/uploads/music/21033102490160641b5d77429.mp3","song":"","pic":null,"author":"","cat":0,"keywords":"","duration":"10:41","uid":0,"add_time":1627994014,"modify_time":1627994014,"delete_flag":1,"up_shelf":0,"robot_id":5,"label":null,"num":233},{"music_id":1841,"collection_id":8,"sort":0,"id":1841,"title":"改善睡眠 Day.14","sfile":"https://resource.soulbuddy.cn/public/uploads/music/21033102493260641b7ceb109.mp3","song":"","pic":null,"author":"","cat":0,"keywords":"","duration":"9:11","uid":0,"add_time":1627994014,"modify_time":1627994014,"delete_flag":1,"up_shelf":0,"robot_id":5,"label":null,"num":233}]
     */

    @SerializedName("collection_info")
    private CollectionInfoBean collectionInfo;
    @SerializedName("music_list")
    private List<AudioItemModel> musicList;

    public CollectionInfoBean getCollectionInfo() {
        return collectionInfo;
    }

    public void setCollectionInfo(CollectionInfoBean collectionInfo) {
        this.collectionInfo = collectionInfo;
    }

    public List<AudioItemModel> getMusicList() {
        return musicList;
    }

    public void setMusicList(List<AudioItemModel> musicList) {
        this.musicList = musicList;
    }

    public static class CollectionInfoBean implements Serializable {
        /**
         * id : 478
         * intr : 排除杂念，找回优质睡眠
         * detail : <p><br></p><p>睡眠是我们的精力之源，如何保证优质睡眠是当今社会一大难题。</p><p><br></p><p>很多时候，杂念太多，可能会让我们难以入睡，即便睡眠时间充足，第二天也会觉得毫无活力。</p><p><br></p><p>本系列将引导你如何识别睡意、排除杂念，进而提高夜晚的睡眠质量。</p><p><br></p><p><strong>适用人群：</strong></p><p>被失眠困扰的人</p><p>睡眠质量差的人</p><p><br></p>
         * num : 8598
         * img : http://resource.soulbuddy.cn/public/uploads/musicPic/2104010748476065b31fe45ce.jpg
         * sort : 520
         * robot_id : 7
         * add_time : 2021-08-20 17:51:21
         * title : 14天改善睡眠
         * recommend_on : 0
         * up_shelf : 1
         * money : 1
         * root_id : 8
         * miniprogram_show_name : 14天改善睡眠
         * music_size : 14
         * listen_history_size : 0
         * collected : 0
         */

        @SerializedName("id")
        private int id;
        @SerializedName("intr")
        private String intr;
        @SerializedName("detail")
        private String detail;
        @SerializedName("num")
        private int num;
        @SerializedName("img")
        private String img;
        @SerializedName("sort")
        private int sort;
        @SerializedName("robot_id")
        private int robotId;
        @SerializedName("add_time")
        private String addTime;
        @SerializedName("title")
        private String title;
        @SerializedName("recommend_on")
        private int recommendOn;
        @SerializedName("up_shelf")
        private int upShelf;
        @SerializedName("money")
        private int money;
        @SerializedName("root_id")
        private int rootId;
        @SerializedName("miniprogram_show_name")
        private String miniprogramShowName;
        @SerializedName("music_size")
        private int musicSize;
        @SerializedName("last_index")
        private int listenHistorySize;
        @SerializedName("collected")
        private int collected;
        @SerializedName("last_music_id")
        private int lastMusicId;

        public int getLastMusicId() {
            return lastMusicId;
        }

        public void setLastMusicId(int lastMusicId) {
            this.lastMusicId = lastMusicId;
        }

        public int getListenHistorySize() {
            return listenHistorySize > 0 ? listenHistorySize : 1;
        }

        public void setListenHistorySize(int listenHistorySize) {
            this.listenHistorySize = listenHistorySize;
        }

        public int getLastHourIndex() {
            return listenHistorySize > 0 ? listenHistorySize - 1 : 0;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getIntr() {
            return intr;
        }

        public void setIntr(String intr) {
            this.intr = intr;
        }

        public String getDetail() {
            return detail;
        }

        public void setDetail(String detail) {
            this.detail = detail;
        }

        public int getNum() {
            return num;
        }

        public void setNum(int num) {
            this.num = num;
        }

        public String getImg() {
            return img;
        }

        public void setImg(String img) {
            this.img = img;
        }

        public int getSort() {
            return sort;
        }

        public void setSort(int sort) {
            this.sort = sort;
        }

        public int getRobotId() {
            return robotId;
        }

        public void setRobotId(int robotId) {
            this.robotId = robotId;
        }

        public String getAddTime() {
            return addTime;
        }

        public void setAddTime(String addTime) {
            this.addTime = addTime;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public int getRecommendOn() {
            return recommendOn;
        }

        public void setRecommendOn(int recommendOn) {
            this.recommendOn = recommendOn;
        }

        public int getUpShelf() {
            return upShelf;
        }

        public void setUpShelf(int upShelf) {
            this.upShelf = upShelf;
        }

        public int getMoney() {
            return money;
        }

        public void setMoney(int money) {
            this.money = money;
        }

        public int getRootId() {
            return rootId;
        }

        public void setRootId(int rootId) {
            this.rootId = rootId;
        }

        public String getMiniprogramShowName() {
            return miniprogramShowName;
        }

        public void setMiniprogramShowName(String miniprogramShowName) {
            this.miniprogramShowName = miniprogramShowName;
        }

        public int getMusicSize() {
            return musicSize;
        }

        public void setMusicSize(int musicSize) {
            this.musicSize = musicSize;
        }

        public int getCollected() {
            return collected;
        }

        public void setCollected(int collected) {
            this.collected = collected;
        }
    }

    public static class AudioItemModel implements Serializable {
        // mp3 待播放列表(应对播放失败的问题)
        public List<MusicModel> mp3WaitList;

        public int status = -1;
        /**
         * music_id : 1828
         * collection_id : 8
         * sort : 0
         * id : 1828
         * title : 改善睡眠 Day.1
         * sfile : http://resource.soulbuddy.cn/public/uploads/music/2103310232146064176e13704.mp3
         * song :
         * pic : null
         * author :
         * cat : 0
         * keywords :
         * duration : 13:43
         * uid : 0
         * add_time : 1627994013
         * modify_time : 1627994013
         * delete_flag : 1
         * up_shelf : 1
         * robot_id : 5
         * label : null
         * num : 938
         */

        @SerializedName("music_id")
        private int musicId;
        @SerializedName("collection_id")
        private int collectionId;
        @SerializedName("sort")
        private int sort;
        @SerializedName("id")
        private int id;
        @SerializedName("title")
        private String title;
        @SerializedName("sfile")
        private String sfile;
        @SerializedName("song")
        private String song;
        @SerializedName("pic")
        private Object pic;
        @SerializedName("author")
        private String author;
        @SerializedName("cat")
        private int cat;
        @SerializedName("keywords")
        private String keywords;
        @SerializedName("duration")
        private String duration;
        @SerializedName("uid")
        private int uid;
        @SerializedName("add_time")
        private int addTime;
        @SerializedName("modify_time")
        private int modifyTime;
        @SerializedName("delete_flag")
        private int deleteFlag;
        @SerializedName("up_shelf")
        private int upShelf;
        @SerializedName("robot_id")
        private int robotId;
        @SerializedName("label")
        private Object label;
        @SerializedName("num")
        private int num;
        @SerializedName("img")
        private Object img;
        @SerializedName("mp3PicId")
        private String mp3PicId;

        public void setMp3PicId(String mp3PicId) {
            this.mp3PicId = mp3PicId;
        }

        public String getMp3PicId() {
            return mp3PicId;
        }

        // 音乐MP3 加载歌词
        private boolean mp3;
        // 自定义 字段
        // 自动返回;
        private boolean autoFinished;
        // 任务回执
        private boolean taskCallBack;

        public boolean isTaskCallBack() {
            return taskCallBack;
        }

        public void setTaskCallBack(boolean taskCallBack) {
            this.taskCallBack = taskCallBack;
        }

        public boolean isAutoFinished() {
            return autoFinished;
        }

        public void setAutoFinished(boolean autoFinished) {
            this.autoFinished = autoFinished;
        }

        public boolean isMp3() {
            return mp3;
        }

        public void setMp3(boolean mp3) {
            this.mp3 = mp3;
        }

        public int getMusicId() {
            return musicId;
        }

        public void setMusicId(int musicId) {
            this.musicId = musicId;
        }

        public int getCollectionId() {
            return collectionId;
        }

        public void setCollectionId(int collectionId) {
            this.collectionId = collectionId;
        }

        public int getSort() {
            return sort;
        }

        public void setSort(int sort) {
            this.sort = sort;
        }

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

        public Object getImg() {
            return img;
        }

        public void setImg(Object img) {
            this.img = img;
        }

        public String getSong() {
            return song;
        }

        public void setSong(String song) {
            this.song = song;
        }

        public Object getPic() {
            return pic;
        }

        public void setPic(Object pic) {
            this.pic = pic;
        }

        public String getAuthor() {
            return author;
        }

        public void setAuthor(String author) {
            this.author = author;
        }

        public int getCat() {
            return cat;
        }

        public void setCat(int cat) {
            this.cat = cat;
        }

        public String getKeywords() {
            return keywords;
        }

        public void setKeywords(String keywords) {
            this.keywords = keywords;
        }

        public String getDuration() {
            return duration;
        }

        public void setDuration(String duration) {
            this.duration = duration;
        }

        public int getUid() {
            return uid;
        }

        public void setUid(int uid) {
            this.uid = uid;
        }

        public int getAddTime() {
            return addTime;
        }

        public void setAddTime(int addTime) {
            this.addTime = addTime;
        }

        public int getModifyTime() {
            return modifyTime;
        }

        public void setModifyTime(int modifyTime) {
            this.modifyTime = modifyTime;
        }

        public int getDeleteFlag() {
            return deleteFlag;
        }

        public void setDeleteFlag(int deleteFlag) {
            this.deleteFlag = deleteFlag;
        }

        public int getUpShelf() {
            return upShelf;
        }

        public void setUpShelf(int upShelf) {
            this.upShelf = upShelf;
        }

        public int getRobotId() {
            return robotId;
        }

        public void setRobotId(int robotId) {
            this.robotId = robotId;
        }

        public Object getLabel() {
            return label;
        }

        public void setLabel(Object label) {
            this.label = label;
        }

        public int getNum() {
            return num;
        }

        public void setNum(int num) {
            this.num = num;
        }
    }
}
