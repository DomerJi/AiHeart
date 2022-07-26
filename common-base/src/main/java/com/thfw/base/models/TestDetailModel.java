package com.thfw.base.models;

import android.text.TextUtils;
import android.util.Log;

import com.google.gson.annotations.SerializedName;
import com.thfw.base.base.IModel;
import com.thfw.base.utils.EmptyUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Author:pengs
 * Date: 2021/12/8 11:41
 * Describe:Todo
 */
public class TestDetailModel implements IModel {


    /**
     * subject_list : [{"id":29471,"rid":380,"did":29470,"des":"","option":"A","answer":"没有"},{"id":29472,"rid":380,"did":29470,"des":"","option":"B","answer":"极少"},{"id":29473,"rid":380,"did":29470,"des":"","option":"C","answer":"一般"},{"id":29474,"rid":380,"did":29470,"des":"","option":"D","answer":"有时"},{"id":29475,"rid":380,"did":29470,"des":"","option":"E","answer":"经常"},{"id":29477,"rid":380,"did":29476,"des":"","option":"A","answer":"没有"},{"id":29478,"rid":380,"did":29476,"des":"","option":"B","answer":"极少"},{"id":29479,"rid":380,"did":29476,"des":"","option":"C","answer":"一般"},{"id":29480,"rid":380,"did":29476,"des":"","option":"D","answer":"有时"},{"id":29481,"rid":380,"did":29476,"des":"","option":"E","answer":"经常"},{"id":29484,"rid":380,"did":29483,"des":"","option":"A","answer":"没有"},{"id":29485,"rid":380,"did":29483,"des":"","option":"B","answer":"极少"},{"id":29486,"rid":380,"did":29483,"des":"","option":"C","answer":"一般"},{"id":29487,"rid":380,"did":29483,"des":"","option":"D","answer":"有时"},{"id":29488,"rid":380,"did":29483,"des":"","option":"E","answer":"经常"},{"id":29491,"rid":380,"did":29490,"des":"","option":"A","answer":"没有"},{"id":29492,"rid":380,"did":29490,"des":"","option":"B","answer":"极少"},{"id":29493,"rid":380,"did":29490,"des":"","option":"C","answer":"一般"},{"id":29494,"rid":380,"did":29490,"des":"","option":"D","answer":"有时"},{"id":29495,"rid":380,"did":29490,"des":"","option":"E","answer":"经常"},{"id":29498,"rid":380,"did":29497,"des":"","option":"A","answer":"没有"},{"id":29499,"rid":380,"did":29497,"des":"","option":"B","answer":"极少"},{"id":29500,"rid":380,"did":29497,"des":"","option":"C","answer":"一般"},{"id":29501,"rid":380,"did":29497,"des":"","option":"D","answer":"有时"},{"id":29502,"rid":380,"did":29497,"des":"","option":"E","answer":"经常"},{"id":29506,"rid":380,"did":29505,"des":"","option":"A","answer":"没有"},{"id":29507,"rid":380,"did":29505,"des":"","option":"B","answer":"极少"},{"id":29508,"rid":380,"did":29505,"des":"","option":"C","answer":"一般"},{"id":29509,"rid":380,"did":29505,"des":"","option":"D","answer":"有时"},{"id":29510,"rid":380,"did":29505,"des":"","option":"E","answer":"经常"},{"id":29513,"rid":380,"did":29512,"des":"","option":"A","answer":"没有"},{"id":29514,"rid":380,"did":29512,"des":"","option":"B","answer":"极少"},{"id":29515,"rid":380,"did":29512,"des":"","option":"C","answer":"一般"},{"id":29516,"rid":380,"did":29512,"des":"","option":"D","answer":"有时"},{"id":29517,"rid":380,"did":29512,"des":"","option":"E","answer":"经常"},{"id":29520,"rid":380,"did":29519,"des":"","option":"A","answer":"没有"},{"id":29521,"rid":380,"did":29519,"des":"","option":"B","answer":"极少"},{"id":29522,"rid":380,"did":29519,"des":"","option":"C","answer":"一般"},{"id":29523,"rid":380,"did":29519,"des":"","option":"D","answer":"有时"},{"id":29524,"rid":380,"did":29519,"des":"","option":"E","answer":"经常"},{"id":29527,"rid":380,"did":29526,"des":"","option":"A","answer":"没有"},{"id":29528,"rid":380,"did":29526,"des":"","option":"B","answer":"极少"},{"id":29529,"rid":380,"did":29526,"des":"","option":"C","answer":"一般"},{"id":29530,"rid":380,"did":29526,"des":"","option":"D","answer":"有时"},{"id":29531,"rid":380,"did":29526,"des":"","option":"E","answer":"经常"},{"id":29534,"rid":380,"did":29533,"des":"","option":"A","answer":"没有"},{"id":29535,"rid":380,"did":29533,"des":"","option":"B","answer":"极少"},{"id":29536,"rid":380,"did":29533,"des":"","option":"C","answer":"一般"},{"id":29537,"rid":380,"did":29533,"des":"","option":"D","answer":"有时"},{"id":29538,"rid":380,"did":29533,"des":"","option":"E","answer":"经常"},{"id":29470,"rid":380,"did":0,"des":"我睡眠不好","option":"","answer":""},{"id":29476,"rid":380,"did":0,"des":"我感觉紧张、烦躁和不安","option":"","answer":""},{"id":29483,"rid":380,"did":0,"des":"很小的声音也会使我惊跳","option":"","answer":""},{"id":29490,"rid":380,"did":0,"des":"我对危险的事情保持警觉","option":"","answer":""},{"id":29497,"rid":380,"did":0,"des":"我不愿与人交往","option":"","answer":""},{"id":29505,"rid":380,"did":0,"des":"工作不再引起我的兴趣，我觉得无精打采","option":"","answer":""},{"id":29512,"rid":380,"did":0,"des":"我感到心身疲倦","option":"","answer":""},{"id":29519,"rid":380,"did":0,"des":"我容易被激惹、想发火","option":"","answer":""},{"id":29526,"rid":380,"did":0,"des":"我感觉过度兴奋，我做事冲动而且甘冒风险","option":"","answer":""},{"id":29533,"rid":380,"did":0,"des":"在我的脑海里、梦里常出现某种灾难性事件的场景","option":"","answer":""}]
     * psychtest_info : {"id":380,"title":"军人心理应激自评问卷","tags":null,"keywords":"","intr":"军人的职业性质决定了军人无论在平时还是在战时，都将面临外界的各种刺激、压力，诱发各种应激性心身反应，影响正常心身功能的发挥。\n\n通过本次测试，你将获得：\n您当前心理应激水平的专业评估和分析，您可以了解自己当下的应激状态，并及时进行调整。\n\n测评说明∶\n▲本测试由国内权威心理测评工具研发团队研发并授权使用；\n▲本测试共10题，测试时间约2-3分钟；\n▲为保证测试的准确性，请你安排一个专门且不受打扰的时间，仔细阅读题目，按照自己近期或当下的真实情况，选择最符合的选项。","pic":"http://resource.soulbuddy.cn/public/uploads/testing/210901045817612f40a975e62.jpeg","cat":1,"type":2,"sort":205,"is_plus":1,"uid":0,"add_time":1637826412,"modify_time":0,"delete_flag":1,"num":249,"money":1,"up_shelf":0,"question_type":null,"result_pattern":null,"robot_id":7,"label":null,"miniprogram_show_name":"军人心理应激自评问卷","miniprogram_show_flag":null,"unvalidTime":null,"root_id":0,"necessary":0}
     */

    @SerializedName("psychtest_info")
    private PsychtestInfoBean psychtestInfo;
    @SerializedName("subject_list")
    private List<SubjectListBean> subjectList;

    private List<SubjectListBean> mSubjectArray;

    public List<SubjectListBean> getSubjectArray() {
        if (mSubjectArray != null) {
            return mSubjectArray;
        }
        if (mSubjectArray == null) {
            mSubjectArray = new ArrayList<>();
        }
        if (!EmptyUtil.isEmpty(subjectList)) {
            HashMap<Integer, ArrayList<SubjectListBean>> selectMap = new HashMap<>();
            for (SubjectListBean bean : subjectList) {
                if (bean.did == 0) {
                    if (selectMap.containsKey(bean.id)) {
                        bean.optionArray = selectMap.get(bean.id);
                    } else {
                        selectMap.put(bean.id, new ArrayList<>());
                        bean.optionArray = selectMap.get(bean.id);
                    }
                    mSubjectArray.add(bean);
                } else {
                    if (selectMap.containsKey(bean.did)) {
                        selectMap.get(bean.did).add(bean);
                    } else {
                        selectMap.put(bean.did, new ArrayList<>());
                        selectMap.get(bean.did).add(bean);
                    }
                }
            }
        }
        return mSubjectArray;
    }

    public PsychtestInfoBean getPsychtestInfo() {
        return psychtestInfo;
    }

    public void setPsychtestInfo(PsychtestInfoBean psychtestInfo) {
        this.psychtestInfo = psychtestInfo;
    }

    public List<SubjectListBean> getSubjectList() {
        return subjectList;
    }

    public void setSubjectList(List<SubjectListBean> subjectList) {
        this.subjectList = subjectList;
    }

    public static class PsychtestInfoBean implements Serializable {
        /**
         * id : 380
         * title : 军人心理应激自评问卷
         * tags : null
         * keywords :
         * intr : 军人的职业性质决定了军人无论在平时还是在战时，都将面临外界的各种刺激、压力，诱发各种应激性心身反应，影响正常心身功能的发挥。
         * <p>
         * 通过本次测试，你将获得：
         * 您当前心理应激水平的专业评估和分析，您可以了解自己当下的应激状态，并及时进行调整。
         * <p>
         * 测评说明∶
         * ▲本测试由国内权威心理测评工具研发团队研发并授权使用；
         * ▲本测试共10题，测试时间约2-3分钟；
         * ▲为保证测试的准确性，请你安排一个专门且不受打扰的时间，仔细阅读题目，按照自己近期或当下的真实情况，选择最符合的选项。
         * pic : http://resource.soulbuddy.cn/public/uploads/testing/210901045817612f40a975e62.jpeg
         * cat : 1
         * type : 2
         * sort : 205
         * is_plus : 1
         * uid : 0
         * add_time : 1637826412
         * modify_time : 0
         * delete_flag : 1
         * num : 249
         * money : 1
         * up_shelf : 0
         * question_type : null
         * result_pattern : null
         * robot_id : 7
         * label : null
         * miniprogram_show_name : 军人心理应激自评问卷
         * miniprogram_show_flag : null
         * unvalidTime : null
         * root_id : 0
         * necessary : 0
         */

        @SerializedName("id")
        private int id;
        @SerializedName("title")
        private String title;
        @SerializedName("tags")
        private Object tags;
        @SerializedName("keywords")
        private String keywords;
        @SerializedName("intr")
        private String intr;
        @SerializedName("pic")
        private String pic;
        @SerializedName("cat")
        private int cat;
        @SerializedName("type")
        private int type;
        @SerializedName("sort")
        private int sort;
        @SerializedName("is_plus")
        private int isPlus;
        @SerializedName("uid")
        private int uid;
        @SerializedName("add_time")
        private int addTime;
        @SerializedName("modify_time")
        private int modifyTime;
        @SerializedName("delete_flag")
        private int deleteFlag;
        @SerializedName("num")
        private int num;
        @SerializedName("money")
        private int money;
        @SerializedName("up_shelf")
        private int upShelf;
        @SerializedName("question_type")
        private Object questionType;
        @SerializedName("result_pattern")
        private Object resultPattern;
        @SerializedName("robot_id")
        private int robotId;
        @SerializedName("label")
        private Object label;
        @SerializedName("miniprogram_show_name")
        private String miniprogramShowName;
        @SerializedName("miniprogram_show_flag")
        private Object miniprogramShowFlag;
        @SerializedName("unvalidTime")
        private Object unvalidTime;
        @SerializedName("root_id")
        private int rootId;
        @SerializedName("necessary")
        private int necessary;
        //for_people   will_get          testing_des
        @SerializedName("for_people")
        private String forPeople;
        @SerializedName("will_get")
        private String willGet;
        @SerializedName("testing_des")
        private String testingDes;
        private int collected;

        private List<HintBean> hintBeans;

        public boolean isCollected() {
            return collected == 1;
        }

        public List<HintBean> getHintBeans() {
            if (hintBeans != null) {
                return hintBeans;
            }
            hintBeans = new ArrayList<>();
            if (!TextUtils.isEmpty(forPeople)) {
                hintBeans.add(new HintBean("适合准则", forPeople));
            }
            if (!TextUtils.isEmpty(willGet)) {
                hintBeans.add(new HintBean("你将获得", willGet));
            }
            if (!TextUtils.isEmpty(testingDes)) {
                hintBeans.add(new HintBean("温馨提示", testingDes));
            }
            return hintBeans;
        }

        public boolean isHas3Body() {
            if (TextUtils.isEmpty(testingDes)) {
                return false;
            }
            return true;
        }

        public boolean isHas2Body() {
            if (TextUtils.isEmpty(willGet)) {
                return false;
            }
            return true;
        }

        public boolean isHas1Body() {
            if (TextUtils.isEmpty(forPeople)) {
                return false;
            }
            return true;
        }

        public HintBean getBody1() {
            return new HintBean("适合准则", forPeople);
        }

        public HintBean getBody2() {
            return new HintBean("你将获得", willGet);
        }

        public HintBean getBody3() {
            return new HintBean("温馨提示", testingDes);
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

        public Object getTags() {
            return tags;
        }

        public void setTags(Object tags) {
            this.tags = tags;
        }

        public String getKeywords() {
            return keywords;
        }

        public void setKeywords(String keywords) {
            this.keywords = keywords;
        }

        public String getIntr() {
            return intr;
        }

        public void setIntr(String intr) {
            this.intr = intr;
        }

        public String getPic() {
            return pic;
        }

        public void setPic(String pic) {
            this.pic = pic;
        }

        public int getCat() {
            return cat;
        }

        public void setCat(int cat) {
            this.cat = cat;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public int getSort() {
            return sort;
        }

        public void setSort(int sort) {
            this.sort = sort;
        }

        public int getIsPlus() {
            return isPlus;
        }

        public void setIsPlus(int isPlus) {
            this.isPlus = isPlus;
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

        public int getNum() {
            return num;
        }

        public void setNum(int num) {
            this.num = num;
        }

        public int getMoney() {
            return money;
        }

        public void setMoney(int money) {
            this.money = money;
        }

        public int getUpShelf() {
            return upShelf;
        }

        public void setUpShelf(int upShelf) {
            this.upShelf = upShelf;
        }

        public Object getQuestionType() {
            return questionType;
        }

        public void setQuestionType(Object questionType) {
            this.questionType = questionType;
        }

        public Object getResultPattern() {
            return resultPattern;
        }

        public void setResultPattern(Object resultPattern) {
            this.resultPattern = resultPattern;
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

        public String getMiniprogramShowName() {
            return miniprogramShowName;
        }

        public void setMiniprogramShowName(String miniprogramShowName) {
            this.miniprogramShowName = miniprogramShowName;
        }

        public Object getMiniprogramShowFlag() {
            return miniprogramShowFlag;
        }

        public void setMiniprogramShowFlag(Object miniprogramShowFlag) {
            this.miniprogramShowFlag = miniprogramShowFlag;
        }

        public Object getUnvalidTime() {
            return unvalidTime;
        }

        public void setUnvalidTime(Object unvalidTime) {
            this.unvalidTime = unvalidTime;
        }

        public int getRootId() {
            return rootId;
        }

        public void setRootId(int rootId) {
            this.rootId = rootId;
        }

        public int getNecessary() {
            return necessary;
        }

        public void setNecessary(int necessary) {
            this.necessary = necessary;
        }
    }

    public static class SubjectListBean implements Serializable {
        /**
         * id : 29471
         * rid : 380
         * did : 29470
         * des :
         * option : A
         * answer : 没有
         */

        @SerializedName("id")
        private int id;
        @SerializedName("rid")
        private int rid;
        @SerializedName("did")
        private int did;
        @SerializedName("des")
        private String des;
        @SerializedName("option")
        private String option;
        @SerializedName("answer")
        private String answer;

        private int selectedIndex = -1;
        private List<SubjectListBean> optionArray;

        public int getSelectedIndex() {
            return selectedIndex;
        }

        public void resetSelectedIndex() {
            selectedIndex = -1;
        }

        public void setSelectedIndex(int selectedIndex) {
            this.selectedIndex = selectedIndex;
        }

        public List<SubjectListBean> getOptionArray() {
            return optionArray;
        }

        public SubjectListBean getSelected() {
            if (selectedIndex >= 0 && selectedIndex < optionArray.size()) {
                return optionArray.get(selectedIndex);
            } else {
                // todo 兜底
                Log.e("jsp", "-------------- optionArray.get(0) ---------------");
                return optionArray.get(0);
            }

        }

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

        public int getDid() {
            return did;
        }

        public void setDid(int did) {
            this.did = did;
        }

        public String getDes() {
            return des;
        }

        public void setDes(String des) {
            this.des = des;
        }

        public String getOption() {
            return option;
        }

        public void setOption(String option) {
            this.option = option;
        }

        public String getAnswer() {
            return answer;
        }

        public void setAnswer(String answer) {
            this.answer = answer;
        }
    }

    public static class HintBean {

        public String title;
        public String des;

        public HintBean(String title, String des) {
            this.title = title;
            this.des = des;
        }
    }
}
