package com.thfw.base.role;

import android.app.Activity;

import androidx.fragment.app.Fragment;

public interface PageLimits {

    <T extends Activity> Class<T> getActivity(int action);

    Fragment getFragment(int action);

}
