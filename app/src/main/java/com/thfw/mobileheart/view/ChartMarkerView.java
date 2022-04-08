package com.thfw.mobileheart.view;

import android.content.Context;
import android.widget.TextView;

import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
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
            mTvCharMarkerTitle.setText(charType.getLabel());
            mTvCharMarkerUnit.setText(charType.getUnit() + ":");
            mTvCharMarkerValue.setText(String.valueOf(e.getY()));
        }
        super.refreshContent(e, highlight);
    }

    private void initView() {
        mTvCharMarkerTitle = (TextView) findViewById(R.id.tv_char_marker_title);
        mTvCharMarkerUnit = (TextView) findViewById(R.id.tv_char_marker_unit);
        mTvCharMarkerValue = (TextView) findViewById(R.id.tv_char_marker_value);
    }
}
