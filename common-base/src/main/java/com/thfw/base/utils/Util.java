package com.thfw.base.utils;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.BitmapFactory;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.Point;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.thfw.base.ContextApp;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Util {

    private static final String TAG = "Util";

    public static int computeSampleSize(BitmapFactory.Options options,
                                        int minSideLength, int maxNumOfPixels) {
        int initialSize = computeInitialSampleSize(options, minSideLength,
                maxNumOfPixels);

        int roundedSize;
        if (initialSize <= 8) {
            roundedSize = 1;
            while (roundedSize < initialSize) {
                roundedSize <<= 1;
            }
        } else {
            roundedSize = (initialSize + 7) / 8 * 8;
        }
        return roundedSize;
    }

    private static int computeInitialSampleSize(BitmapFactory.Options options,
                                                int minSideLength, int maxNumOfPixels) {
        double w = options.outWidth;
        double h = options.outHeight;
        int lowerBound = (maxNumOfPixels == -1) ? 1 :
                (int) Math.ceil(Math.sqrt(w * h / maxNumOfPixels));
        int upperBound = (minSideLength == -1) ? 128 :
                (int) Math.min(Math.floor(w / minSideLength),
                        Math.floor(h / minSideLength));

        if (upperBound < lowerBound) {
            // return the larger one when there is no overlapping zone.
            return lowerBound;
        }

        if ((maxNumOfPixels == -1) &&
                (minSideLength == -1)) {
            return 1;
        } else if (minSideLength == -1) {
            return lowerBound;
        } else {
            return upperBound;
        }
    }

    /**
     * dip 转 px
     */
    public static int dipToPx(float dip, Context context) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dip, context.getResources().getDisplayMetrics());
    }

    /**
     * sp 转 px
     */
    public static int spToPx(int sp, Context context) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, context.getResources().getDisplayMetrics());

    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(float pxValue, Context context) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * @return 屏幕密度（像素比例：0.75/1.0/1.5/2.0）
     */
    public static float getDensity(Context context) {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        return dm.density;
    }

    /**
     * 获得屏幕宽度
     */
    public static int getScreenWidth(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.widthPixels;
    }

    /**
     * 获得屏幕高度
     */
    public static int getScreenHeight(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.heightPixels;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    public static int getScreenLogicWidth(Context context) {
        int size = 0;
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point outPoint = new Point();
        display.getRealSize(outPoint);
        size = outPoint.x;
        return size;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    public static int getScreenLogicHeight(Context context) {
        int size = 0;
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point outPoint = new Point();
        display.getRealSize(outPoint);
        size = outPoint.y;
        return size;
    }

    /**
     * 通过反射的方式获取状态栏高度
     *
     * @return
     */
    public static int getStatusBarHeight(Context context) {

        int height = 0;
        int resourceId = context.getApplicationContext().getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            height = context.getApplicationContext().getResources().getDimensionPixelSize(resourceId);
        }
        LogUtil.d("getStatusBarHeight = " + height);
        return height;
    }

    /**
     * 设置某个View的margin
     *
     * @param view   需要设置的view
     * @param isDp   需要设置的数值是否为DP
     * @param left   左边距
     * @param right  右边距
     * @param top    上边距
     * @param bottom 下边距
     * @return
     */
    public static ViewGroup.LayoutParams setViewMargin(View view, boolean isDp, int left, int right, int top, int bottom) {
        if (view == null) {
            return null;
        }
        Context context = view.getContext();
        int leftPx = left;
        int rightPx = right;
        int topPx = top;
        int bottomPx = bottom;
        ViewGroup.LayoutParams params = view.getLayoutParams();
        ViewGroup.MarginLayoutParams marginParams = null;
        //获取view的margin设置参数
        if (params instanceof ViewGroup.MarginLayoutParams) {
            marginParams = (ViewGroup.MarginLayoutParams) params;
        } else {
            //不存在时创建一个新的参数
            marginParams = new ViewGroup.MarginLayoutParams(params);
        }

        //根据DP与PX转换计算值
        if (isDp) {
            leftPx = dipToPx(left, context);
            rightPx = dipToPx(right, context);
            topPx = dipToPx(top, context);
            bottomPx = dipToPx(bottom, context);
        }
        //设置margin
        marginParams.setMargins(leftPx, topPx, rightPx, bottomPx);
        view.setLayoutParams(marginParams);
        view.requestLayout();
        return marginParams;
    }

    public static boolean isPad(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE || isHuaweiPad();
    }

    public static boolean isHuaweiPad() {
        if (Build.BRAND.equals("HUAWEI") && Build.DEVICE.equals("HWBAH3-H")) {
            return true;
        }
        return false;
    }

    /**
     * 英寸
     *
     * @param context
     * @return
     */
    public static double screenInches(Context context) {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        double x = Math.pow(dm.widthPixels / dm.xdpi, 2);
        double y = Math.pow(dm.heightPixels / dm.ydpi, 2);
        return Math.sqrt(x + y);
    }

    /**
     * 添加下划线
     *
     * @param textViews
     */
    public static void addUnderLine(TextView... textViews) {
        for (TextView textView : textViews) {
            textView.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);  // 下划线
            textView.getPaint().setAntiAlias(true); // 抗锯齿
        }
    }

    public static String getAppVersion(Context context) {
        String versionName = "";

        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo p1 = pm.getPackageInfo(context.getPackageName(), 0);
            versionName = p1.versionName;
            if (TextUtils.isEmpty(versionName) || versionName.length() <= 0) {
                return "";
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();

        }
        return versionName;


    }

    public static int getAppVersionCode(Context context) {
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo p1 = pm.getPackageInfo(context.getPackageName(), 0);
            return p1.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return 1;
    }

    /**
     * 是否系统app
     *
     * @param pkgName
     * @return
     */
    public static boolean isSystemApp(String pkgName) {
        boolean isSystemApp = false;
        PackageInfo pi = null;
        try {
            PackageManager pm = ContextApp.get().getPackageManager();
            pi = pm.getPackageInfo(pkgName, 0);
        } catch (Throwable t) {
            Log.w("TAG", t.getMessage(), t);
        }
        // 是系统中已安装的应用
        if (pi != null) {
            boolean isSysApp = (pi.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 1;
            boolean isSysUpd = (pi.applicationInfo.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) == 1;
            isSystemApp = isSysApp || isSysUpd;
        }
        return isSystemApp;
    }

    /**
     * 忘记某一个wifi密码
     *
     * @param wifiManager
     * @param targetSsid
     */
    public static void removeWifiBySsid(WifiManager wifiManager, String targetSsid) {
        Log.i(TAG, "try to removeWifiBySsid, targetSsid=" + targetSsid);
        if (wifiManager == null) {
            return;
        }
        List<WifiConfiguration> wifiConfigs = wifiManager.getConfiguredNetworks();
        if (wifiConfigs == null) {
            return;
        }
        for (WifiConfiguration wifiConfig : wifiConfigs) {
            String ssid = wifiConfig.SSID;
            ssid = ssid.replaceAll("\"", "");
            Log.i(TAG, "removeWifiBySsid ssid=" + ssid);
            if (ssid.equals(targetSsid)) {
                Log.i(TAG, "removeWifiBySsid success, SSID = " + wifiConfig.SSID + " netId = " + String.valueOf(wifiConfig.networkId));
                wifiManager.removeNetwork(wifiConfig.networkId);
                wifiManager.saveConfiguration();
                return;
            }
        }
    }

    public static boolean isSavePassWord(WifiManager wifiManager, String targetSsid) {

        Log.i(TAG, "try to isSavePassWord, targetSsid=" + targetSsid);
        if (wifiManager == null) {
            return false;
        }
        List<WifiConfiguration> wifiConfigs = wifiManager.getConfiguredNetworks();
        if (wifiConfigs == null) {
            return false;
        }

        for (WifiConfiguration wifiConfig : wifiConfigs) {
            String ssid = wifiConfig.SSID;
            ssid = ssid.replaceAll("\"", "");
            Log.i(TAG, "isSavePassWord ssid=" + ssid + ";targetSsid = " + targetSsid);
            Log.i(TAG, "isSavePassWord equals=" + ssid.equals(targetSsid));
            if (ssid.equals(targetSsid)) {
                return true;
            }
        }
        return false;
    }

    public static WifiConfiguration getConfig(WifiManager wifiManager, String targetSsid) {
        Log.i(TAG, "try to getConfig, targetSsid=" + targetSsid);
        if (wifiManager == null) {
            return null;
        }
        List<WifiConfiguration> wifiConfigs = wifiManager.getConfiguredNetworks();
        if (wifiConfigs == null) {
            return null;
        }


        for (WifiConfiguration wifiConfig : wifiConfigs) {
            String ssid = wifiConfig.SSID;
            ssid = ssid.replaceAll("\"", "");
            Log.i(TAG, "getConfig ssid=" + ssid + ";targetSsid = " + targetSsid);
            Log.i(TAG, "getConfig equals=" + ssid.equals(targetSsid));
            if (ssid.equals(targetSsid)) {
                return wifiConfig;
            }
        }
        return null;

    }

    public static int getWeekFormCalendar(long time) {
        Date inputDate = new Date();
        inputDate.setTime(time);
        java.util.Calendar date = java.util.Calendar.getInstance();
        date.setTime(inputDate);
        return date.get(java.util.Calendar.DAY_OF_WEEK) - 1;
    }

    public static void setBlackView(Activity activity) {
        if (!EmptyUtil.isEmpty(activity)) {
            Window window = activity.getWindow();
            if (window != null) {
                setBlackView(window.getDecorView());
            }
        }
    }

    public static void setRgbView(Activity activity) {
        if (!EmptyUtil.isEmpty(activity)) {
            Window window = activity.getWindow();
            if (window != null) {
                setRgbView(window.getDecorView());
            }
        }
    }

    public static void setBlackView(View view) {
        if (view != null) {
            Paint mPaint = new Paint();
            ColorMatrix cm = new ColorMatrix();
            cm.setSaturation(0);
            mPaint.setColorFilter(new ColorMatrixColorFilter(cm));
            view.setLayerType(View.LAYER_TYPE_HARDWARE, mPaint);
        }
    }

    public static void setRgbView(View view) {
        if (view != null) {
            Paint mPaint = new Paint();
            ColorMatrix cm = new ColorMatrix();
            mPaint.setColorFilter(new ColorMatrixColorFilter(cm));
            view.setLayerType(View.LAYER_TYPE_HARDWARE, mPaint);
        }
    }

    /**
     * 获取特定字符串之间的内容
     *
     * @param soap 内容 【123456789】
     * @param rgex 匹配内容 【123(.*?)789】
     * @return 【456】
     */
    public static List<String> getSubUtil(String soap, String rgex) {
        List<String> list = new ArrayList<String>();
        Pattern pattern = Pattern.compile(rgex);
        // 匹配的模式
        Matcher m = pattern.matcher(soap);
        while (m.find()) {
            int i = 1;
            String content = m.group(i);
            if (!TextUtils.isEmpty(content)) {
                list.add(content);
            }
            i++;
        }
        return list;
    }

}