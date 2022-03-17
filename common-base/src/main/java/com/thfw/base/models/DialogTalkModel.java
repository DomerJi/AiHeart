package com.thfw.base.models;

import com.google.gson.annotations.SerializedName;
import com.thfw.base.base.IModel;
import com.thfw.base.utils.EmptyUtil;
import com.thfw.base.utils.HourUtil;

import java.io.Serializable;
import java.util.List;

/**
 * Author:pengs
 * Date: 2021/12/27 14:16
 * Describe:Todo
 * type：1：用户输入 | 2：机器人正常对话回复 | 3：单选题 | 4：自由输入题目 | 5：测评推荐 | 6：视频推荐  | 7：文章推荐
 * <p>
 * scene：对话场景 1：树洞聊天 | 2：咨询助理
 */
public class DialogTalkModel implements IModel {


    /**
     * id : 153
     * user_id : 100001
     * type : 3
     * question : 你好，我是小密，是一个可以帮助你觉察和处理心理问题的智能机器人。在进入主题对话前，我们先简单了解下对方吧~
     * dialog_id : null
     * scene : 2
     * check_radio : [{"key":89722,"value":"好","sort":0,"href":""}]
     * check_value : 0
     * recommend_info : null
     * created_time : 2021-12-27 09:00:44
     * updated_time : null
     */

    @SerializedName("id")
    private int id;
    @SerializedName("user_id")
    private int userId;
    @SerializedName("type")
    private int type;
    @SerializedName("question")
    private String question;
    @SerializedName("dialog_id")
    private Object dialogId;
    @SerializedName("scene")
    private int scene;
    @SerializedName("check_value")
    private int checkValue;
    @SerializedName("recommend_info")
    private RecommendInfoBean recommendInfo;
    @SerializedName("created_time")
    private String createdTime;
    @SerializedName("updated_time")
    private Object updatedTime;
    @SerializedName("check_radio")
    private List<CheckRadioBean> checkRadio;

    private ChatEntity checkEntity;

    public boolean hasCheckRadio() {
        return checkValue > 0 && !EmptyUtil.isEmpty(checkRadio) && getCheckRadio() != null;
    }

    public ChatEntity getMeCheckRadio() {
        if (checkEntity != null) {
            return checkEntity;
        }
        for (CheckRadioBean bean : checkRadio) {
            if (checkValue == bean.getKey()) {
                checkEntity = new ChatEntity();
                checkEntity.type = ChatEntity.TYPE_TO;
                checkEntity.talk = bean.getValue();
                checkEntity.time = getTimeMills();
                break;
            }
        }
        return checkEntity;
    }

    private long timeMills;

    public long getTimeMills() {
        if (timeMills <= 0) {
            if (createdTime != null) {
                timeMills = HourUtil.getYYMMDD_HHMMSS(createdTime);
            } else {
                timeMills = System.currentTimeMillis() - 100000;
            }
        }
        return timeMills;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public Object getDialogId() {
        return dialogId;
    }

    public void setDialogId(Object dialogId) {
        this.dialogId = dialogId;
    }

    /**
     * 主题对话or树洞
     *
     * @param originScene
     * @return
     */
    public int getScene(int originScene) {
        return scene > 0 ? scene : originScene;
    }

    public void setScene(int scene) {
        this.scene = scene;
    }

    public int getCheckValue() {
        return checkValue;
    }

    public void setCheckValue(int checkValue) {
        this.checkValue = checkValue;
    }

    public RecommendInfoBean getRecommendInfo() {
        return recommendInfo;
    }

    public void setRecommendInfo(RecommendInfoBean recommendInfo) {
        this.recommendInfo = recommendInfo;
    }

    public String getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(String createdTime) {
        this.createdTime = createdTime;
    }

    public Object getUpdatedTime() {
        return updatedTime;
    }

    public void setUpdatedTime(Object updatedTime) {
        this.updatedTime = updatedTime;
    }

    public List<CheckRadioBean> getCheckRadio() {
        return checkRadio;
    }

    public void setCheckRadio(List<CheckRadioBean> checkRadio) {
        this.checkRadio = checkRadio;
    }

    public static class ArticleInfoBean implements IModel {

        /**
         * art_id : 548
         * img_path : http://resource.soulbuddy.cn/public/images/long-distance-relationship.jpg
         * title : 身为军人的我，这3点和女友相处很重要
         */

        @SerializedName("art_id")
        private String artId;
        @SerializedName("img_path")
        private String imgPath;
        @SerializedName("title")
        private String title;

        public String getArtId() {
            return artId;
        }

        public void setArtId(String artId) {
            this.artId = artId;
        }

        public String getImgPath() {
            return imgPath;
        }

        public void setImgPath(String imgPath) {
            this.imgPath = imgPath;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }
    }

    public static class RecommendInfoBean implements IModel {

        /**
         * id : 380
         * title : 军人心理应激自评问卷
         * type : 2
         * num : 331
         * source : http://resource.soulbuddy.cn/public/uploads/testing/210901045817612f40a975e62.jpeg
         */

        @SerializedName("id")
        private int id;
        @SerializedName("title") // 标题
        private String title;
        @SerializedName("type") // 类型 目前测评可见
        private int type;
        @SerializedName("num") // 测评数量 音频合集练习数量
        private int num;
        @SerializedName("music_size") // 音频合集课时总数
        private int musicSize;
        @SerializedName("file") // 音频单首 |视频 地址
        private String file;
        @SerializedName("img") // 图片地址
        private String img;

        public int getId() {
            return id;
        }

        public String getTitle() {
            return title;
        }

        public int getType() {
            return type;
        }

        public int getNum() {
            return num;
        }

        public int getMusicSize() {
            return musicSize;
        }

        public String getFile() {
            return file;
        }

        public String getImg() {
            return img;
        }
    }

    public static class CheckRadioBean implements Serializable {
        /**
         * key : 89722
         * value : 好
         * sort : 0
         * href :
         */

        @SerializedName("key")
        private int key;
        @SerializedName("value")
        private String value;
        @SerializedName("sort")
        private int sort;
        @SerializedName("href")
        private String href;

        public int getKey() {
            return key;
        }

        public void setKey(int key) {
            this.key = key;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public int getSort() {
            return sort;
        }

        public void setSort(int sort) {
            this.sort = sort;
        }

        public String getHref() {
            return href;
        }

        public void setHref(String href) {
            this.href = href;
        }

        @Override
        public String toString() {
            return "CheckRadioBean{" +
                    "key=" + key +
                    ", value='" + value + '\'' +
                    ", sort=" + sort +
                    ", href='" + href + '\'' +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "DialogTalkModel{" +
                "id=" + id +
                ", userId=" + userId +
                ", type=" + type +
                ", question='" + question + '\'' +
                ", dialogId=" + dialogId +
                ", scene=" + scene +
                ", checkValue=" + checkValue +
                ", recommendInfo=" + recommendInfo +
                ", createdTime='" + createdTime + '\'' +
                ", updatedTime=" + updatedTime +
                ", checkRadio=" + checkRadio +
                '}';
    }
}
