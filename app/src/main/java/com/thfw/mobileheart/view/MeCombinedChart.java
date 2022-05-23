package com.thfw.mobileheart.view;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;

import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IDataSet;
import com.thfw.base.models.MoodActiveModel;

/**
 * This chart class allows the combination of lines, bars, scatter and candle
 * data all displayed in one chart area.
 *
 * @author Philipp Jahoda
 */
public class MeCombinedChart extends CombinedChart {


    public MeCombinedChart(Context context) {
        super(context);
    }

    public MeCombinedChart(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MeCombinedChart(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    /**
     * draws all MarkerViews on the highlighted positions
     */
    @Override
    protected void drawMarkers(Canvas canvas) {

        // if there is no marker view or drawing marker is disabled
        if (mMarker == null || !isDrawMarkersEnabled() || !valuesToHighlight())
            return;

        for (int i = 0; i < mIndicesToHighlight.length; i++) {

            Highlight highlight = mIndicesToHighlight[i];

            IDataSet set = mData.getDataSetByHighlight(highlight);

            Entry e = mData.getEntryForHighlight(highlight);
            if (e == null)
                continue;

            if (e.getData() instanceof MoodActiveModel.MoodImp) {
                MoodActiveModel.MoodImp moodImp = (MoodActiveModel.MoodImp) e.getData();
                if (moodImp.model != null && (moodImp.model.isSetTime() || moodImp.model.isEmpty(moodImp.type))) {
                    continue;
                }
            }
            int entryIndex = set.getEntryIndex(e);

            // make sure entry not null
            if (entryIndex > set.getEntryCount() * mAnimator.getPhaseX())
                continue;

            float[] pos = getMarkerPosition(highlight);

            // check bounds
            if (!mViewPortHandler.isInBounds(pos[0], pos[1]))
                continue;

            // callbacks to update the content
            mMarker.refreshContent(e, highlight);

            // draw the marker
            mMarker.draw(canvas, pos[0], pos[1]);
        }
    }

}
