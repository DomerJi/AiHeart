package com.thfw.mobileheart.fragment.exercise;

import android.widget.TextView;

import androidx.core.text.HtmlCompat;

import com.thfw.base.base.IPresenter;
import com.thfw.base.models.ExerciseModel;
import com.thfw.mobileheart.R;
import com.thfw.ui.base.BaseFragment;

/**
 * Author:pengs
 * Date: 2021/10/8 17:25
 * Describe:Todo
 */
public class ExerciseHintFragment extends BaseFragment {

    ExerciseModel exerciseModel;
    private TextView mTvHint;

    public void setExerciseModel(ExerciseModel exerciseModel) {
        this.exerciseModel = exerciseModel;
        setTvHint();
    }

    @Override
    public int getContentView() {
        return R.layout.fragment_exercisehint_layout;
    }

    @Override
    public IPresenter onCreatePresenter() {
        return null;
    }

    @Override
    public void initView() {

        mTvHint = (TextView) findViewById(R.id.tv_hint);
        setTvHint();
    }

    @Override
    public void initData() {

    }

    public void setTvHint() {
        if (mTvHint != null && exerciseModel != null) {
            String hint = exerciseModel.getDesc();
            mTvHint.setText(HtmlCompat.fromHtml(hint, HtmlCompat.FROM_HTML_MODE_LEGACY));
        }
    }
}
