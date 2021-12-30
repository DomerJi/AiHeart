package com.thfw.base.models;

import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;
import com.thfw.base.api.HistoryApi;
import com.thfw.base.base.IModel;
import com.thfw.base.utils.RegularUtil;

/**
 * Author:pengs
 * Date: 2021/12/9 16:12
 * Describe:Todo
 */
public class HistoryModel implements IModel {

    private int type = -1;

    public int getType() {
        if (type != -1) {
            return type;
        }

        if (this instanceof HistoryTestModel) {
            type = HistoryApi.TYPE_TEST;
        } else if (this instanceof HistoryExerciseModel) {
            type = HistoryApi.TYPE_EXERCISE;
        } else if (this instanceof HistoryBookModel) {
            type = HistoryApi.TYPE_BOOK;
        } else if (this instanceof HistoryAudioModel) {
            type = HistoryApi.TYPE_AUDIO;
        } else if (this instanceof HistoryStudyModel) {
            type = HistoryApi.TYPE_STUDY;
        } else if (this instanceof HistoryVideoModel) {
            type = HistoryApi.TYPE_VIDEO;
        } else {
            type = HistoryApi.TYPE_VIDEO;
        }
        return type;
    }

    public static class HistoryTestModel extends HistoryModel {

        /**
         * id : 5
         * rid : 380
         * user_id : 100001
         * add_time : 2021-12-08 08:45:15
         * status : 0
         * spend_time : 33
         * valid : 0
         * pic : http://resource.soulbuddy.cn/public/uploads/testing/210901045817612f40a975e62.jpeg
         * title : 军人心理应激自评问卷
         * num : 305
         */

        @SerializedName("id")
        private int id;
        @SerializedName("rid")
        private int rid;
        @SerializedName("user_id")
        private int userId;
        @SerializedName("add_time")
        private String addTime;
        @SerializedName("status")
        private int status;
        @SerializedName("spend_time")
        private int spendTime;
        @SerializedName("valid")
        private int valid;
        @SerializedName("pic")
        private String pic;
        @SerializedName("title")
        private String title;
        @SerializedName("num")
        private int num;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getRid() {
            return rid;
        }

        public void setRid(int rid) {
            this.rid = rid;
        }

        public int getUserId() {
            return userId;
        }

        public void setUserId(int userId) {
            this.userId = userId;
        }

        public String getAddTime() {
            return addTime;
        }

        public void setAddTime(String addTime) {
            this.addTime = addTime;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public int getSpendTime() {
            return spendTime;
        }

        public void setSpendTime(int spendTime) {
            this.spendTime = spendTime;
        }

        public int getValid() {
            return valid;
        }

        public void setValid(int valid) {
            this.valid = valid;
        }

        public String getPic() {
            return pic;
        }

        public void setPic(String pic) {
            this.pic = pic;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public int getNum() {
            return num;
        }

        public void setNum(int num) {
            this.num = num;
        }
    }


    public static class HistoryVideoModel extends HistoryModel {

        /**
         * add_time : 2021-12-17 03:27:14
         * id : 406
         * title : 抑郁症动画暖心小故事
         */

        @SerializedName("add_time")
        private String addTime;
        @SerializedName("id")
        private int id;
        @SerializedName("title")
        private String title;

        public String getAddTime() {
            return addTime;
        }

        public void setAddTime(String addTime) {
            this.addTime = addTime;
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
    }

    public static class HistoryAudioModel extends HistoryModel {


        /**
         * add_time : 2021-12-17 01:11:54
         * id : 1828
         * title : 改善睡眠 Day.1
         * collection_id : 473
         */

        @SerializedName("add_time")
        private String addTime;
        @SerializedName("id")
        private int id;
        @SerializedName("title")
        private String title;
        @SerializedName("collection_id")
        private String collectionId;

        public String getAddTime() {
            return addTime;
        }

        public void setAddTime(String addTime) {
            this.addTime = addTime;
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

        public int getCollectionId() {
            if (TextUtils.isEmpty(collectionId) || !RegularUtil.isNumber(collectionId)) {
                return 0;
            }
            return Integer.parseInt(collectionId);
        }

        public void setCollectionId(String collectionId) {
            this.collectionId = collectionId;
        }
    }

    public static class HistoryStudyModel extends HistoryModel {

    }

    public static class HistoryBookModel extends HistoryModel {


        /**
         * add_time : 2021-12-30 02:25:07
         * id : 563
         * title : 强迫性赌博是什么？
         */

        @SerializedName("add_time")
        private String addTime;
        @SerializedName("id")
        private int id;
        @SerializedName("title")
        private String title;

        public String getAddTime() {
            return addTime;
        }

        public void setAddTime(String addTime) {
            this.addTime = addTime;
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
    }

    public static class HistoryExerciseModel extends HistoryModel {

    }
}
