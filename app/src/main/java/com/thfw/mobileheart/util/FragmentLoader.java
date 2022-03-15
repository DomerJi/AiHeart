package com.thfw.mobileheart.util;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.util.HashMap;
import java.util.Map;

/**
 * Fragment加载切换
 * Created By jishuaipeng on 2021/4/27
 */
public class FragmentLoader {

    private FragmentManager mFragmentManager;
    private int mContentLayoutId;
    // 临时存放参数
    private HashMap<String, String> parameter = new HashMap<>();
    private Map<Integer, FragmentItem> mFragments = new HashMap<Integer, FragmentItem>();

    private Fragment mCurrentFragment;

    public FragmentLoader(FragmentManager fragmentManager, int contentLayoutId) {
        this.mFragmentManager = fragmentManager;
        this.mContentLayoutId = contentLayoutId;
    }

    public FragmentLoader put(String key, String value) {
        parameter.put(key, value);
        return this;
    }

    public String get(String key) {
        return parameter.get(key);
    }

    public FragmentLoader add(int id, Fragment fragment) {
        if (fragment != null && !mFragments.containsKey(id)) {
            mFragments.put(id, new FragmentItem(fragment));
        }
        return this;
    }

    public void hide() {
        if (mCurrentFragment != null) {
            FragmentTransaction transaction = mFragmentManager.beginTransaction();
            transaction.hide(mCurrentFragment).commit();
        }
    }

    public Fragment load(int id) {
        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        FragmentItem item = mFragments.get(id);
        if (item == null) {
            return null;
        }
        hideCurrentFragment(transaction);
        mCurrentFragment = item.fragment != null ? item.fragment : mFragmentManager.findFragmentByTag(item.tag);
        if (mCurrentFragment == null) {
            try {
                Class cls = Class.forName(item.fragmentCls.getName());
                mCurrentFragment = (Fragment) cls.newInstance();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (mCurrentFragment == null) {
            return null;
        }

        if (!mCurrentFragment.isAdded()) {
            transaction.add(mContentLayoutId, mCurrentFragment, item.tag);
        }

        transaction.show(mCurrentFragment).commit();
        return mCurrentFragment;
    }


    private void hideCurrentFragment(FragmentTransaction transaction) {
        if (mCurrentFragment != null) {
            transaction.hide(mCurrentFragment);
        } else {
            // 解决偶现crash fragment 重叠问题
            for (FragmentItem mItem : mFragments.values()) {
                if (mItem != null) {
                    mCurrentFragment = mFragmentManager.findFragmentByTag(mItem.tag);
                    if (mCurrentFragment != null) {
                        transaction.hide(mCurrentFragment);
                    }
                }
            }
        }
    }

    public Fragment getCurrentFragment() {
        return mCurrentFragment;
    }

    private class FragmentItem {

        public String tag;
        public Class fragmentCls;
        public Fragment fragment;

        public FragmentItem(Fragment fragment) {
            this.fragment = fragment;
            this.tag = fragment.getClass().getCanonicalName();
            this.fragmentCls = fragment.getClass();
        }

    }
}
