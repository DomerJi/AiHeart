package com.thfw.robotheart.activitys;

import com.thfw.base.base.IPresenter;
import com.thfw.robotheart.lhxk.LhXkHelper;
import com.thfw.ui.base.IBaseFragment;
import com.thfw.ui.widget.DeviceUtil;

/**
 * Author:pengs
 * Date: 2021/11/15 10:23
 * Describe:Todo
 */
public abstract class RobotBaseFragment<T extends IPresenter> extends IBaseFragment<T> {

    public static final int VOICE_STATIC = 0;
    public static final int VOICE_CHANGED = 1;

    @Override
    public void onVisible(boolean isVisible) {
        super.onVisible(isVisible);
        if (DeviceUtil.isLhXk_OS_R_SD01B()) {
            if (isVisible) {
                initLocalVoice(VOICE_STATIC);
            } else {
                clearLocalVoice(VOICE_STATIC);
            }
        }

    }

    protected void initLocalVoice(int type) {

    }

    protected void clearLocalVoice(int type) {
        LhXkHelper.removeAction(this.getClass());
    }
}
