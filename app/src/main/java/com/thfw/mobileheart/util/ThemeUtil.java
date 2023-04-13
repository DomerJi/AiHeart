package com.thfw.mobileheart.util;

import static com.thfw.base.ThemeType.TYPE_APP;
import static com.thfw.base.ThemeType.TYPE_APP_SPLASH;
import static com.thfw.base.ThemeType.TYPE_APP_WHITE;

import android.content.Context;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.thfw.base.models.PageStateModel;
import com.thfw.mobileheart.R;
import com.thfw.ui.utils.PageStateViewModel;

/**
 * Author:pengs
 * Date: 2023/1/17 11:07
 * Describe:Todo
 */
public class ThemeUtil {

    public static final int NOT_THEME_ID = -1;

    public static int getThemeId(Context context, int type) {
        if (!(context instanceof AppCompatActivity)) {
            return NOT_THEME_ID;
        }
        PageStateViewModel pageStateViewModel = new ViewModelProvider((AppCompatActivity) context).get(PageStateViewModel.class);
        if (pageStateViewModel.getPageStateLive() == null || pageStateViewModel.getPageStateLive().getValue() == null) {
            return NOT_THEME_ID;
        }
        PageStateModel model = pageStateViewModel.getPageStateLive().getValue();
        if (model.holidays == null) {
            return NOT_THEME_ID;
        }
        long star = model.holidays.getStart();
        long end = model.holidays.getEnd();
        Log.i("ThemeUtil", "star = " + end + "   -   end = " + end);
        if (star == -1 || end == -1) {
            return NOT_THEME_ID;
        }
//        Calendar calendarStart = Calendar.getInstance();
//        calendarStart.setTimeInMillis(star);
//
//        Calendar calendarEnd = Calendar.getInstance();
//        calendarEnd.setTimeInMillis(end);
        long millis = System.currentTimeMillis();
        if (millis >= star && millis <= end) {
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
        int id = getThemeId(context, type);
        if (id != NOT_THEME_ID) {
            context.setTheme(id);
        }

    }

}
