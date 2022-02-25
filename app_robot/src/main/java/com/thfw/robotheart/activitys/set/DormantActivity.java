package com.thfw.robotheart.activitys.set;

import android.content.Context;
import android.content.Intent;
import android.view.MotionEvent;
import android.widget.TextView;

import com.opensource.svgaplayer.SVGAImageView;
import com.thfw.base.base.IPresenter;
import com.thfw.robotheart.R;
import com.thfw.robotheart.activitys.RobotBaseActivity;
import com.thfw.robotheart.constants.AnimFileName;
import com.thfw.robotheart.util.Dormant;
import com.thfw.robotheart.view.DialogRobotFactory;
import com.thfw.robotheart.view.SVGAHelper;

public class DormantActivity extends RobotBaseActivity implements Dormant.MinuteChangeListener {

    private SVGAImageView mIvAnim;
    private android.widget.TextView mTvTime;
    private String mOriginalText;

    private String[] mEmojiFileNames = new String[]{
            AnimFileName.EMOJI_CHUMO,
            AnimFileName.EMOJI_HUANXING,
            AnimFileName.EMOJI_KAIJI,
            AnimFileName.EMOJI_SHIWANG,
            AnimFileName.EMOJI_SPEECH,
            AnimFileName.EMOJI_WELCOM,
            AnimFileName.EMOJI_XIUMIAN,
            AnimFileName.EMOJI_XUANYUN,
            AnimFileName.EMOJI_QINGTING,
    };

    private int testIndex = 0;

    public static void startActivity(Context context) {
        context.startActivity(new Intent(context, DormantActivity.class));
    }

    @Override
    public int getContentView() {
        return R.layout.activity_dormant;
    }

    @Override
    public IPresenter onCreatePresenter() {
        return null;
    }

    @Override
    public void initView() {

        mIvAnim = (SVGAImageView) findViewById(R.id.iv_anim);
        mTvTime = (TextView) findViewById(R.id.tv_time);
        mOriginalText = mTvTime.getText().toString();

        testEmojiLoop(mEmojiFileNames[testIndex]);

//        SVGAHelper.playSVGA(mIvAnim, AnimFileName.EMOJI_XIUMIAN, 0, new DialogRobotFactory.SimpleSVGACallBack() {
//            @Override
//            public void onFinished() {
//
//            }
//        });
    }

    private void testEmojiLoop(String filename) {
        SVGAHelper.playSVGA(mIvAnim, filename, 1, new DialogRobotFactory.SimpleSVGACallBack() {
            @Override
            public void onFinished() {
                testEmojiLoop(mEmojiFileNames[++testIndex % mEmojiFileNames.length]);
            }
        });
    }

    @Override
    public void initData() {
        Dormant.setMinuteChangeListener(this);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        finish();
        return super.dispatchTouchEvent(event);
    }

    @Override
    public void onDestroy() {
        Dormant.setMinuteChangeListener(null);
        super.onDestroy();
    }

    @Override
    public void onChange() {
        mTvTime.setText(mOriginalText + "（已休眠" + Dormant.getSleepTime() + "分钟）");
    }
}