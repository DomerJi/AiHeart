package com.thfw.mobileheart.view;

import android.content.Context;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.makeramen.roundedimageview.RoundedImageView;
import com.thfw.mobileheart.R;
import com.thfw.mobileheart.constants.CharType;

/**
 * Author:pengs
 * Date: 2022/4/8 13:18
 * Describe:Todo
 */
public class ChartMarkerView extends MarkerView {

    private TextView mTvCharMarkerTitle;
    private TextView mTvCharMarkerUnit;
    private TextView mTvCharMarkerValue;
    private ConstraintLayout mClMarketTime;
    private ConstraintLayout mClMarketMood;
    private LinearLayout mLlMood;
    private RoundedImageView mRivEmoji;
    private TextView mTvMoodValue;

    /**
     * Constructor. Sets up the MarkerView with a custom layout resource.
     *
     * @param context
     * @param layoutResource the layout resource to use for the MarkerView
     */
    public ChartMarkerView(Context context, int layoutResource) {
        super(context, layoutResource);
        initView();
    }

    @Override
    public void refreshContent(Entry e, Highlight highlight) {
        if (e.getData() instanceof CharType) {
            CharType charType = (CharType) e.getData();
            if (charType.getType() == CharType.MOOD.getType()) {
                mClMarketMood.setVisibility(VISIBLE);
                mClMarketTime.setVisibility(GONE);
            } else {
                mClMarketMood.setVisibility(GONE);
                mClMarketTime.setVisibility(VISIBLE);
                mTvCharMarkerTitle.setText(charType.getLabel());
                mTvCharMarkerUnit.setText(charType.getUnit() + ":");
                mTvCharMarkerValue.setText(String.valueOf(e.getY()));
            }
        }
        super.refreshContent(e, highlight);
    }

    private void initView() {
        mTvCharMarkerTitle = (TextView) findViewById(R.id.tv_char_marker_title);
        mTvCharMarkerUnit = (TextView) findViewById(R.id.tv_char_marker_unit);
        mTvCharMarkerValue = (TextView) findViewById(R.id.tv_char_marker_value);
        mClMarketTime = (ConstraintLayout) findViewById(R.id.cl_market_time);
        mClMarketMood = (ConstraintLayout) findViewById(R.id.cl_market_mood);
        mLlMood = (LinearLayout) findViewById(R.id.ll_mood);
        mRivEmoji = (RoundedImageView) findViewById(R.id.riv_emoji);
        mTvMoodValue = (TextView) findViewById(R.id.tv_mood_value);
    }
}
