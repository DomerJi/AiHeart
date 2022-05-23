package com.thfw.mobileheart.view;

import android.content.Context;
import android.graphics.Canvas;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.MPPointF;
import com.thfw.base.models.CharType;
import com.thfw.base.models.MoodActiveModel;
import com.thfw.mobileheart.R;
import com.thfw.ui.utils.GlideUtil;

import java.util.HashMap;

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
    private ImageView mRivEmoji;
    private TextView mTvMoodValue;

    private int maxHeight;

    /**
     * Constructor. Sets up the MarkerView with a custom layout resource.
     *
     * @param context
     * @param layoutResource the layout resource to use for the MarkerView
     */
    public ChartMarkerView(Context context, int layoutResource) {
        super(context, layoutResource);
        initView();
        setOffset(2, -35);
    }

    public void setMaxHeight(int maxHeight) {
        this.maxHeight = maxHeight;
    }

    @Override
    public void refreshContent(Entry e, Highlight highlight) {
        if (e.getData() instanceof MoodActiveModel.MoodImp) {
            MoodActiveModel.MoodImp moodImp = (MoodActiveModel.MoodImp) e.getData();

            if (moodImp.type.getType() == CharType.MOOD.getType()) {
                mClMarketMood.setVisibility(VISIBLE);
                mClMarketTime.setVisibility(GONE);
                mTvMoodValue.setText(moodImp.model.getMoodName());
                if (TextUtils.isEmpty(moodImp.model.getMoodPic())) {
                    GlideUtil.load(getContext(), R.drawable.gray_cirlle_bg, mRivEmoji);
                } else {
                    Integer localPic = getPic(moodImp.model.getMoodName());
                    if (localPic != null) {
                        mRivEmoji.setImageResource(localPic);
                    } else {
                        Glide.with(getContext())
                                .load(moodImp.model.getMoodPic())
                                .apply(RequestOptions
                                        .placeholderOf(R.drawable.gray_cirlle_bg)
                                        .error(R.drawable.gray_cirlle_bg)
                                        .fallback(R.drawable.gray_cirlle_bg))
                                .centerCrop().into(mRivEmoji);
                    }
                }
            } else {
                mClMarketMood.setVisibility(GONE);
                mClMarketTime.setVisibility(VISIBLE);
                mTvCharMarkerTitle.setText(moodImp.type.getLabel());
                mTvCharMarkerUnit.setText(moodImp.type.getUnit() + ":");
                mTvCharMarkerValue.setText(String.valueOf((int) e.getY()));
            }
        }
        super.refreshContent(e, highlight);
    }

    private Integer getPic(String name) {

        HashMap<String, Integer> hashMap = new HashMap<>();
        hashMap.put("惊喜", R.mipmap.jingxi);
        hashMap.put("自豪", R.mipmap.zihao);
        hashMap.put("开心", R.mipmap.kaixin);
        hashMap.put("满足", R.mipmap.manzu);
        hashMap.put("轻松", R.mipmap.qingsong);
        hashMap.put("平静", R.mipmap.pingjing);
        hashMap.put("凑合", R.mipmap.couhe);
        hashMap.put("迷茫", R.mipmap.mimang);
        hashMap.put("紧张", R.mipmap.jinzhang);
        hashMap.put("生气", R.mipmap.shengqi);
        hashMap.put("抑郁", R.mipmap.yiyu);
        hashMap.put("痛苦", R.mipmap.tongku);

        return hashMap.get(name);
    }

    private void initView() {
        mTvCharMarkerTitle = (TextView) findViewById(R.id.tv_char_marker_title);
        mTvCharMarkerUnit = (TextView) findViewById(R.id.tv_char_marker_unit);
        mTvCharMarkerValue = (TextView) findViewById(R.id.tv_char_marker_value);
        mClMarketTime = (ConstraintLayout) findViewById(R.id.cl_market_time);
        mClMarketMood = (ConstraintLayout) findViewById(R.id.cl_market_mood);
        mLlMood = (LinearLayout) findViewById(R.id.ll_mood);
        mRivEmoji = (ImageView) findViewById(R.id.riv_emoji);
        mTvMoodValue = (TextView) findViewById(R.id.tv_mood_value);
    }

    @Override
    public void draw(Canvas canvas, float posX, float posY) {
        if (posY > maxHeight - 200) {
            posY = maxHeight - 200;
        }

        MPPointF offset = getOffsetForDrawingAtPoint(posX, posY);

        int saveId = canvas.save();
        // translate to the correct position and draw
        canvas.translate(posX + offset.x, posY + offset.y);
        draw(canvas);
        canvas.restoreToCount(saveId);
    }
}
