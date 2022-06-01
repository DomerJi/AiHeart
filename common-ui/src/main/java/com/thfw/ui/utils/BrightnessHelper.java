package com.thfw.ui.utils;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.provider.Settings;
import android.view.Window;
import android.view.WindowManager;

import com.thfw.base.utils.Util;

/**
 * Author: yanzhikai
 * Description: 用于辅助调节亮度的类
 * Email: yanzhikai_yjk@qq.com
 */

public class BrightnessHelper {
    private ContentResolver resolver;
    private int maxBrightness = 255;
    private final boolean isSystemApp;

    public BrightnessHelper(Context context) {
        resolver = context.getContentResolver();
        isSystemApp = Util.isSystemApp(context.getPackageName());

    }

    /*
     * 调整亮度范围
     */
    private int adjustBrightnessNumber(int brightness) {
        if (brightness < 0) {
            brightness = 0;
        } else if (brightness > 255) {
            brightness = 255;
        }
        return brightness;
    }

    /*
     * 关闭自动调节亮度
     */
    public void offAutoBrightness() {
        try {
            if (Settings.System.getInt(resolver, Settings.System.SCREEN_BRIGHTNESS_MODE) == Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC) {
                Settings.System.putInt(resolver,
                        Settings.System.SCREEN_BRIGHTNESS_MODE,
                        Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
            }
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
    }

    /*
     * 关闭自动调节亮度
     */
    public boolean isOnAutoBrightness() {
        if (isSystemApp) {
            try {
                return Settings.System.getInt(resolver, Settings.System.SCREEN_BRIGHTNESS_MODE) == Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC;
            } catch (Settings.SettingNotFoundException e) {
                return false;
            }
        } else {
            return false;
        }
    }

    /*
     * 开启自动调节亮度
     */
    public void onAutoBrightness() {
        try {
            if (Settings.System.getInt(resolver, Settings.System.SCREEN_BRIGHTNESS_MODE) == Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL) {
                Settings.System.putInt(resolver,
                        Settings.System.SCREEN_BRIGHTNESS_MODE,
                        Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC);
            }
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
    }

    /*
     * 获取系统亮度
     */
    public float getBrightness() {
        return Settings.System.getInt(resolver, Settings.System.SCREEN_BRIGHTNESS, 255) / 255f;
    }

    /*
     * 获取系统亮度
     */
    public float getAutoBrightness(Activity activity) {
        float light = 0;
        if (isOnAutoBrightness()) {
            Window mWindow = activity.getWindow();
            if (mWindow != null) {
                light = mWindow.getAttributes().screenBrightness;
                if (light > 0) {
                    return light;
                }
            }
            light = Settings.System.getFloat(resolver, "screen_auto_brightness_adj", 0f);
            if (light > 0) {
                return light;
            }
        }
        return Settings.System.getInt(resolver, Settings.System.SCREEN_BRIGHTNESS, 255) / 255f;
    }

    /*
     * 设置系统亮度，如果有设置了自动调节，请先调用offAutoBrightness()方法关闭自动调节，否则会设置失败
     */
    private void setSystemBrightness(int newBrightness) {
        Settings.System.putInt(resolver, Settings.System.SCREEN_BRIGHTNESS
                , adjustBrightnessNumber(newBrightness));
    }

    //设置当前APP的亮度
    public void setBrightness(float brightnessPercent, Activity activity) {
        if (isSystemApp) {
            setSystemBrightness((int) (brightnessPercent * 255));
        } else {
            Window window = activity.getWindow();
            WindowManager.LayoutParams layoutParams = window.getAttributes();
            layoutParams.screenBrightness = brightnessPercent;
            window.setAttributes(layoutParams);
        }
    }
}
