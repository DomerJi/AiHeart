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
import com.thfw.net.CommonInterceptor;
import com.thfw.net.ResponeThrowable;
import com.thfw.presenter.LoginPresenter;
import com.thfw.util.ContextApp;
import com.thfw.util.LogUtil;
import com.thfw.util.SharePreferenceUtil;
import com.trello.rxlifecycle2.LifecycleProvider;

import java.text.SimpleDateFormat;
import java.util.Locale;

public final class YmHandler {


    private static final String ORGAN_ID = "1";

    private static OnYmLoginCallBack statciOnYmLoginCallBack;
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

    public static void setOnYmLoginCallBack(OnYmLoginCallBack onYmLoginCallBack) {
        statciOnYmLoginCallBack = onYmLoginCallBack;
    }


    private static void init(Context context, String userId, String phone, String nickname, OnYmLoginCallBack onYmLoginCallBack) {
        ContextApp.init(context.getApplicationContext());
        ContextApp.setDeviceType(ContextApp.DeviceType.ROBOT);
        String packageName = context.getPackageName();
        String openId = packageName + "_" + ORGAN_ID + "_" + userId;
        token = SharePreferenceUtil.getString(openId, null);
        if (!TextUtils.isEmpty(token)) {
            onYmLoginCallBack.onSuccess();
            if (statciOnYmLoginCallBack != null) {
                statciOnYmLoginCallBack.onSuccess();
            }
        } else {
            if (context instanceof FragmentActivity) {
                LoadingDialog.show((FragmentActivity) context, "加载中");
            }

            new LoginPresenter(new LoginPresenter.LoginUi<TokenModel>() {
                @Override
                public LifecycleProvider getLifecycleProvider() {
                    return null;
                }

                @Override
                public void onSuccess(TokenModel data) {
                    LoadingDialog.hide();
                    if (data == null || TextUtils.isEmpty(data.token)) {
                        onFail(new ResponeThrowable(-99, "login data null"));
                        return;
                    }
                    token = data.token;
                    SharePreferenceUtil.setString(openId, data.token);
                    onYmLoginCallBack.onSuccess();
                    if (statciOnYmLoginCallBack != null) {
                        statciOnYmLoginCallBack.onSuccess();
                    }
                }

                @Override
                public void onFail(ResponeThrowable throwable) {
                    LoadingDialog.hide();
                    onYmLoginCallBack.onFail(throwable.getCode(), throwable.getMessage());
                    if (statciOnYmLoginCallBack != null) {
                        statciOnYmLoginCallBack.onFail(throwable.getCode(), throwable.getMessage());
                    }
                }
            }).loginByPassword("16630007656", "123456");
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

    private YmHandler() {

    }
}