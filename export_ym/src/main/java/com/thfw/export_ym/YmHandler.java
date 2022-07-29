package com.thfw.export_ym;

import android.content.Context;
import android.text.TextUtils;

import androidx.fragment.app.FragmentActivity;

import com.scwang.smart.refresh.footer.ClassicsFooter;
import com.scwang.smart.refresh.header.ClassicsHeader;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.scwang.smart.refresh.layout.api.RefreshFooter;
import com.scwang.smart.refresh.layout.api.RefreshHeader;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.DefaultRefreshFooterCreator;
import com.scwang.smart.refresh.layout.listener.DefaultRefreshHeaderCreator;
import com.thfw.dialog.LoadingDialog;
import com.thfw.export_ym.test.TestReportActivity;
import com.thfw.export_ym.test.TestingActivity;
import com.thfw.models.TokenModel;
import com.thfw.net.BaseCodeListener;
import com.thfw.net.CommonInterceptor;
import com.thfw.net.OkHttpUtil;
import com.thfw.net.ResponeThrowable;
import com.thfw.presenter.LoginPresenter;
import com.thfw.util.AESUtils3;
import com.thfw.util.ContextApp;
import com.thfw.util.EmptyUtil;
import com.thfw.util.LogUtil;
import com.thfw.util.SharePreferenceUtil;
import com.trello.rxlifecycle2.LifecycleProvider;

import java.text.SimpleDateFormat;
import java.util.Locale;

public final class YmHandler {

    //手机号
    private static final String REGEX_PHONE = "0?(13|14|15|16|17|18|19)[0-9]{9}";
    private static String appKey;
    private static OnYmLoginCallBack statciOnYmLoginCallBack;
    private static OnYmLoginLoadingListener onYmLoginLoadingListener;
    private static String token;

    static {
        CommonInterceptor.setTokenListener(new CommonInterceptor.OnTokenListener() {
            @Override
            public String getToken() {
                return token;
            }
        });

        // 设置全局的Header构建器
        SmartRefreshLayout.setDefaultRefreshHeaderCreator(new DefaultRefreshHeaderCreator() {
            @Override
            public RefreshHeader createRefreshHeader(Context context, RefreshLayout layout) {
                return new ClassicsHeader(context)
                        .setTimeFormat(new SimpleDateFormat("更新于 MM-dd HH:mm", Locale.CHINA));
            }
        });
        // 设置全局的Footer构建器
        SmartRefreshLayout.setDefaultRefreshFooterCreator(new DefaultRefreshFooterCreator() {
            @Override
            public RefreshFooter createRefreshFooter(Context context, RefreshLayout layout) {
                // 指定为经典Footer，默认是 BallPulseFooter
                return new ClassicsFooter(context).setDrawableSize(20);
            }
        });

    }


    private YmHandler() {

    }

    // 9HgJ5TNZEMmuoNDuxfRjIg==
    public static void setAppKey(String appKey) {
        YmHandler.appKey = appKey;
    }

    public static void setOnYmLoginCallBack(OnYmLoginCallBack onYmLoginCallBack) {
        YmHandler.statciOnYmLoginCallBack = onYmLoginCallBack;
    }

    public static void setOnYmLoginLoadingListener(OnYmLoginLoadingListener onYmLoginLoadingListener) {
        YmHandler.onYmLoginLoadingListener = onYmLoginLoadingListener;
    }

    private static void init(Context context, String userId, String phone, String userName, OnYmLoginCallBack onYmLoginCallBack) {
        ContextApp.init(context.getApplicationContext());
        if (EmptyUtil.isEmpty(appKey)) {
            onYmLoginCallBack.onFail(-7, "参数错误-7");
            return;
        }
        String packageName = context.getPackageName();
        String organId = AESUtils3.decrypt(appKey, "900677d13b86e89d01dafe179f5b36ce");
        String openId = packageName + "_" + organId + "_" + userId;
        token = SharePreferenceUtil.getString(openId, null);
        OkHttpUtil.setBaseCodeListener(new BaseCodeListener() {
            @Override
            public void onCode(int code) {
                SharePreferenceUtil.setString(openId, "");
                init(context, userId, phone, userName, onYmLoginCallBack);
            }
        });
        if (!TextUtils.isEmpty(token)) {
            onYmLoginCallBack.onSuccess();
            if (statciOnYmLoginCallBack != null) {
                statciOnYmLoginCallBack.onSuccess();
            }
        } else {
            if (!EmptyUtil.isEmpty(phone)) {
                if (!phone.matches(REGEX_PHONE)) {
                    onYmLoginCallBack.onFail(-8, "参数错误-8");
                    return;
                }
            }
            if (context instanceof FragmentActivity) {
                if (YmHandler.onYmLoginLoadingListener == null) {
                    LoadingDialog.show((FragmentActivity) context, "加载中");
                } else {
                    YmHandler.onYmLoginLoadingListener.show();
                }
            }
            new LoginPresenter(new LoginPresenter.LoginUi<TokenModel>() {
                @Override
                public LifecycleProvider getLifecycleProvider() {
                    return null;
                }

                @Override
                public void onSuccess(TokenModel data) {
                    if (YmHandler.onYmLoginLoadingListener != null) {
                        YmHandler.onYmLoginLoadingListener.hide();
                    }
                    LoadingDialog.hide();
                    if (data == null || TextUtils.isEmpty(data.token)) {
                        onFail(new ResponeThrowable(-99, "login data null"));
                        return;
                    }
                    token = data.token;
                    SharePreferenceUtil.setString(openId, data.token);
                    onYmLoginCallBack.onSuccess();
                    if (YmHandler.onYmLoginLoadingListener != null) {
                        YmHandler.onYmLoginLoadingListener.hide();
                    }
                    if (YmHandler.statciOnYmLoginCallBack != null) {
                        YmHandler.statciOnYmLoginCallBack.onSuccess();
                    }

                }

                @Override
                public void onFail(ResponeThrowable throwable) {
                    LoadingDialog.hide();
                    onYmLoginCallBack.onFail(throwable.getCode(), throwable.getMessage());
                    if (YmHandler.statciOnYmLoginCallBack != null) {
                        YmHandler.statciOnYmLoginCallBack.onFail(throwable.getCode(), throwable.getMessage());
                    }
                }
            }).loginByOpenId(phone, appKey, userName, openId);
        }
    }

    public static void startMentalTest(Context context, String userId, String phone, String nickname) {

        LogUtil.e("startMentalTest begin +++++++++++++++++++++++");
        init(context, userId, phone, nickname, new OnYmLoginCallBack() {
            @Override
            public void onSuccess() {
                TestingActivity.startActivity(context);
                LogUtil.e("startMentalTest onSuccess");
            }

            @Override
            public void onFail(int code, String error) {
                LogUtil.e("startMentalTest onFail -> code = " + code + " ; error = " + error);
            }
        });

    }

    public static void startMentalTestPortList(Context context, String userId, String phone, String nickname) {
        LogUtil.e("startMentalTestPortList begin+++++++++++++++++++++++ ");
        init(context, userId, phone, nickname, new OnYmLoginCallBack() {
            @Override
            public void onSuccess() {
                TestReportActivity.startActivity(context);
                LogUtil.e("startMentalTestPortList onSuccess ");
            }

            @Override
            public void onFail(int code, String error) {
                LogUtil.e("startMentalTestPortList onFail -> code = " + code + " ; error = " + error);
            }
        });
    }


    public interface OnYmLoginCallBack {

        void onSuccess();

        void onFail(int code, String error);

    }

    public interface OnYmLoginLoadingListener {

        void show();

        void hide();

    }
}