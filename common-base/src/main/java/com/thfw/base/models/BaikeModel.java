package com.thfw.base.models;

import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Author:pengs
 * Date: 2022/8/29 16:29
 * Describe:Todo
 */
public class BaikeModel implements Serializable {


    /**
     * id : 34021
     * subLemmaId : 18032609
     * newLemmaId : 632714
     * key : 李克强
     * desc : 中共第十九届中央政治局常委，国务院总理、党组书记
     * title : 李克强
     * card : [{"key":"m67_nameC","name":"中文名","value":["李克强"],"format":["李克强"]},{"key":"m67_nation","name":"国籍","value":["<a target=_blank href=\"/item/%E4%B8%AD%E5%9B%BD/22516505\" data-lemmaid=\"22516505\">中国<\/a>"],"format":["<a target=_blank href=\"/item/%E4%B8%AD%E5%9B%BD/22516505\" data-lemmaid=\"22516505\">中国<\/a>"]},{"key":"m67_ethnic","name":"民族","value":["汉族"],"format":["汉族"]},{"key":"m67_nativePlace","name":"籍贯","value":["安徽定远"],"format":["安徽定远"]},{"key":"m67_bornDay","name":"出生日期","value":["1955年7月"],"format":["1955年7月"]},{"key":"m67_school","name":"毕业院校","value":["<a target=_blank href=\"/item/%E5%8C%97%E4%BA%AC%E5%A4%A7%E5%AD%A6/110221\" data-lemmaid=\"110221\">北京大学<\/a>"],"format":["<a target=_blank href=\"/item/%E5%8C%97%E4%BA%AC%E5%A4%A7%E5%AD%A6/110221\" data-lemmaid=\"110221\">北京大学<\/a>"]},{"key":"m67_politicalAffiliation","name":"政治面貌","value":["中共党员"],"format":["中共党员"]}]
     * image : https://bkimg.cdn.bcebos.com/pic/024f78f0f736afc310868e32b819ebc4b64512da?x-bce-process=image/format,f_auto
     * src : 024f78f0f736afc310868e32b819ebc4b64512da
     * imageHeight : 900
     * imageWidth : 730
     * isSummaryPic : y
     * abstract : 李克强，男，汉族，1955年7月生，安徽定远人，1974年3月参加工作，1976年5月加入中国共产党，北京大学法律系和经济学院经济学专业毕业，在职研究生学历，法学学士、经济学博士学位。现任中共第十九届中央政治局常委，国务院总理、党组书记。
     * moduleIds : [4116915797,4116915799]
     * url : http://baike.baidu.com/view/34021.htm
     * wapUrl : http://wapbaike.baidu.com/item/%E6%9D%8E%E5%85%8B%E5%BC%BA/632714
     * hasOther : 0
     * catalog : ["<a href='http://baike.baidu.com/view/34021.htm#1'>人物履历<\/a>","<a href='http://baike.baidu.com/view/34021.htm#2'>担任职务<\/a>"]
     * wapCatalog : ["<a href='http://wapbaike.baidu.com/item/%E6%9D%8E%E5%85%8B%E5%BC%BA/632714#1'>人物履历<\/a>","<a href='http://wapbaike.baidu.com/item/%E6%9D%8E%E5%85%8B%E5%BC%BA/632714#2'>担任职务<\/a>"]
     * logo : http://img.baidu.com/img/baike/logo-baike.gif
     * copyrights : 以上内容来自百度百科平台，由百度百科网友创作。
     * customImg :
     */

    @SerializedName("id")
    private int id;
    @SerializedName("subLemmaId")
    private int subLemmaId;
    @SerializedName("newLemmaId")
    private int newLemmaId;
    @SerializedName("key")
    private String key;
    @SerializedName("desc")
    private String desc;
    @SerializedName("title")
    private String title;
    @SerializedName("image")
    private String image;
    @SerializedName("src")
    private String src;
    @SerializedName("imageHeight")
    private int imageHeight;
    @SerializedName("imageWidth")
    private int imageWidth;
    @SerializedName("isSummaryPic")
    private String isSummaryPic;
    @SerializedName("abstract")
    private String abstractX;
    @SerializedName("url")
    private String url;
    @SerializedName("wapUrl")
    private String wapUrl;
    @SerializedName("hasOther")
    private int hasOther;
    @SerializedName("logo")
    private String logo;
    @SerializedName("copyrights")
    private String copyrights;
    @SerializedName("customImg")
    private String customImg;
    @SerializedName("card")
    private List<CardBean> card;
    @SerializedName("moduleIds")
    private List<Long> moduleIds;
    @SerializedName("catalog")
    private List<String> catalog;
    @SerializedName("wapCatalog")
    private List<String> wapCatalog;

    public static class CardBean implements Serializable {
        /**
         * key : m67_nameC
         * name : 中文名
         * value : ["李克强"]
         * format : ["李克强"]
         */

        @SerializedName("key")
        private String key;
        @SerializedName("name")
        private String name;
        @SerializedName("value")
        private List<String> value;
        @SerializedName("format")
        private List<String> format;
    }

    public boolean isDescNull() {
        return TextUtils.isEmpty(desc) && TextUtils.isEmpty(abstractX);
    }

    public String getDesc() {
        if (TextUtils.isEmpty(abstractX)) {
            return desc;
        }
        return desc + "\n" + abstractX;
    }
}
