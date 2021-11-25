package com.thfw.ui.voice;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Author:pengs
 * Date: 2021/11/22 16:59
 * Describe:Todo
 */
public class VoiceHolderView {
    public int type;
    public ImageView mImage;
    public TextView mText;

    public VoiceHolderView(View itemView) {

    }

    public interface Type {
        /**
         * 拟人
         */
        int TYPE_PERSONIFICATION = 0;
        int TYPE_TOOL = 1;
        int TYPE_HOME = 2;
    }
}
