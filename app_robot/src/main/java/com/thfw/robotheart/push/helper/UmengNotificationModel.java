package com.thfw.robotheart.push.helper;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Author:pengs
 * Date: 2022/2/18 10:21
 * Describe:Todo
 */
public class UmengNotificationModel implements Serializable {


    /**
     * display_type : notification
     * body : {"after_open":"go_app","ticker":"强J test","title":"强J test","play_sound":"true","text":"强J content218-012345"}
     * msg_id : um8vxd9164515052717311
     */

    @SerializedName("display_type")
    private String displayType;
    @SerializedName("body")
    private BodyBean body;
    @SerializedName("msg_id")
    private String msgId;

    public String getDisplayType() {
        return displayType;
    }

    public void setDisplayType(String displayType) {
        this.displayType = displayType;
    }

    public BodyBean getBody() {
        return body;
    }

    public void setBody(BodyBean body) {
        this.body = body;
    }

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public static class BodyBean implements Serializable {
        /**
         * after_open : go_app
         * ticker : 强J test
         * title : 强J test
         * play_sound : true
         * text : 强J content218-012345
         */

        @SerializedName("after_open")
        private String afterOpen;
        @SerializedName("ticker")
        private String ticker;
        @SerializedName("title")
        private String title;
        @SerializedName("play_sound")
        private String playSound;
        @SerializedName("text")
        private String text;

        public String getAfterOpen() {
            return afterOpen;
        }

        public void setAfterOpen(String afterOpen) {
            this.afterOpen = afterOpen;
        }

        public String getTicker() {
            return ticker;
        }

        public void setTicker(String ticker) {
            this.ticker = ticker;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getPlaySound() {
            return playSound;
        }

        public void setPlaySound(String playSound) {
            this.playSound = playSound;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }
    }
}
