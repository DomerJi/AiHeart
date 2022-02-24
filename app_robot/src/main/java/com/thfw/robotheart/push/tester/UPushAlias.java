package com.thfw.robotheart.push.tester;

import android.content.Context;

import com.thfw.base.utils.LogUtil;
import com.thfw.robotheart.MyApplication;
import com.thfw.robotheart.push.MyPreferences;
import com.umeng.message.PushAgent;
import com.umeng.message.api.UPushAliasCallback;
import com.umeng.message.api.UPushTagCallback;
import com.umeng.message.common.inter.ITagManager;
import com.umeng.message.tag.TagManager;

import java.util.List;


public class UPushAlias {

    private static final String TAG = "UPushAlias";

    /**
     * 增加别名
     */
    public static void add(Context context, String alias, String type) {
        PushAgent.getInstance(context).addAlias(alias, type, new UPushAliasCallback() {
            @Override
            public void onMessage(boolean success, String message) {
                LogUtil.i(TAG, "add success:" + success + " msg:" + message);
            }
        });
    }

    /**
     * 删除别名
     */
    public static void delete(Context context, String alias, String type) {
        PushAgent.getInstance(context).deleteAlias(alias, type, new UPushAliasCallback() {
            @Override
            public void onMessage(boolean success, String message) {
                LogUtil.i(TAG, "delete success:" + success + " msg:" + message);
            }
        });
    }

    /**
     * 绑定别名
     */
    public static void set(final Context context, String alias, String type) {
        LogUtil.d(TAG, "PushAgent 设置别名 start alias = " + alias);
        if (hasAgreedAgreement()) {
            PushAgent.getInstance(context).setAlias(alias, type, new UPushAliasCallback() {
                @Override
                public void onMessage(final boolean success, String message) {
                    LogUtil.i(TAG, "PushAgent 设置别名 set success:" + success + " msg:" + message);
                }
            });
        }
        LogUtil.d(TAG, "PushAgent 设置别名 end alias = " + alias);
    }

    /**
     * 别名和Tag功能测试入口
     */
    public static void test(Context context) {
        set(context, "123456", "uid");
    }


    public static void setTag(int id) {
        LogUtil.d(TAG, "PushAgent 添加标签 start id = " + id);
        if (hasAgreedAgreement()) {
            LogUtil.d(TAG, "PushAgent 添加标签 hasAgreedAgreement = " + true);
            //获取服务器端的所有标签
            PushAgent.getInstance(MyApplication.getApp()).getTagManager().getTags(new TagManager.TagListCallBack() {

                @Override
                public void onMessage(boolean isSuccess, List<String> result) {
                    LogUtil.d(TAG, "PushAgent ，添加标签[getTags] isSuccess = " + isSuccess);
                    if (!isSuccess) {
                        return;
                    }
                    if (result.size() == 0) {
                        PushAgent.getInstance(MyApplication.getApp()).getTagManager().addTags(new UPushTagCallback<ITagManager.Result>() {
                            @Override
                            public void onMessage(boolean isSuccess, ITagManager.Result result) {
                                LogUtil.d(TAG, "PushAgent 添加标签 isSuccess = " + isSuccess + ", result = " + result.msg);
                            }
                        }, "organ_" + id);
                        return;
                    } else {
                        if (result.size() == 1 && result.contains("organ_" + id)) {
                            LogUtil.d(TAG, "PushAgent ，添加标签[已有无需添加] isSuccess = " + isSuccess);
                            return;
                        }
                    }
                    //删除标签,将之前添加的标签中的一个或多个删除
                    PushAgent.getInstance(MyApplication.getApp()).getTagManager().deleteTags(new TagManager.TCallBack() {
                        @Override
                        public void onMessage(final boolean isSuccess, final ITagManager.Result result) {
                            LogUtil.d(TAG, "PushAgent 添加标签[deleteTags] isSuccess = " + isSuccess + ", result = " + result.msg);
                            PushAgent.getInstance(MyApplication.getApp()).getTagManager().addTags(new UPushTagCallback<ITagManager.Result>() {
                                @Override
                                public void onMessage(boolean isSuccess, ITagManager.Result result) {
                                    LogUtil.d(TAG, "PushAgent 添加标签 isSuccess = " + isSuccess + ", result = " + result.msg);
                                }
                            }, "organ_" + id);
                        }
                    }, result.toArray(new String[result.size()]));
                }
            });
        }
        LogUtil.d(TAG, "PushAgent 添加标签 end");
    }


    private static boolean hasAgreedAgreement() {
        return MyPreferences.getInstance(MyApplication.getApp()).hasAgreePrivacyAgreement();
    }

}
