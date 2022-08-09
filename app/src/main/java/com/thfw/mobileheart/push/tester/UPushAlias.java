package com.thfw.mobileheart.push.tester;

import android.content.Context;

import com.thfw.base.utils.LogUtil;
import com.thfw.mobileheart.MyApplication;
import com.thfw.mobileheart.push.MyPreferences;
import com.umeng.message.PushAgent;
import com.umeng.message.api.UPushAliasCallback;
import com.umeng.message.api.UPushTagCallback;
import com.umeng.message.common.inter.ITagManager;


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
            PushAgent.getInstance(context).deleteAlias(alias, type, new UPushAliasCallback() {
                @Override
                public void onMessage(boolean b, String s) {
                    PushAgent.getInstance(context).setAlias(alias, type, new UPushAliasCallback() {
                        @Override
                        public void onMessage(final boolean success, String message) {
                            LogUtil.i(TAG, "PushAgent 设置别名 set success:" + success + " msg:" + message);
                        }
                    });
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
            PushAgent.getInstance(MyApplication.getApp()).getTagManager().addTags(new UPushTagCallback<ITagManager.Result>() {
                @Override
                public void onMessage(boolean isSuccess, ITagManager.Result result) {
                    LogUtil.d(TAG, "PushAgent 添加标签 isSuccess = " + isSuccess);
                }
            }, "organ_" + id);
        }
        LogUtil.d(TAG, "PushAgent 添加标签 end");
    }


    private static boolean hasAgreedAgreement() {
        return MyPreferences.getInstance(MyApplication.getApp()).hasAgreePrivacyAgreement();
    }

}
