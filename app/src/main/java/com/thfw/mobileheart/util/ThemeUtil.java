package com.thfw.mobileheart.util;

import static com.thfw.base.ThemeType.TYPE_APP;
import static com.thfw.base.ThemeType.TYPE_APP_SPLASH;
import static com.thfw.base.ThemeType.TYPE_APP_WHITE;

import android.content.Context;
import android.util.Log;

import com.thfw.mobileheart.R;

import java.util.Calendar;

/**
 * Author:pengs
 * Date: 2023/1/17 11:07
 * Describe:Todo
 */
public class ThemeUtil {

    public static final int NOT_THEME_ID = -1;

    public static int getThemeId(int type) {

        Calendar calendarStart = Calendar.getInstance();
        calendarStart.set(2023, 0, 21, 0, 0, 0);

        Calendar calendarEnd = Calendar.getInstance();
        calendarEnd.set(2023, 0, 27, 0, 0, 0);
        long millis = System.currentTimeMillis();
        if (millis >= calendarStart.getTimeInMillis() && millis <= calendarEnd.getTimeInMillis()) {
            switch (type) {
                case TYPE_APP:
                    return R.style.AppTheme_NewYear;
                case TYPE_APP_WHITE:
                    return R.style.AppThemeWhite_NewYear;
                case TYPE_APP_SPLASH:
                    return R.style.AppThemeSplash_NewYear;
            }
            Log.i("ThemeUtil", "TYPE -> " + type);
        }
        return NOT_THEME_ID;
    }

    public static void setTheme(Context context, int type) {
        int id = getThemeId(type);
        if (id != NOT_THEME_ID) {
            context.setTheme(id);
        }

    }

}
