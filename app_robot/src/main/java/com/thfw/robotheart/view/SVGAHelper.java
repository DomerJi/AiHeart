package com.thfw.robotheart.view;

import com.opensource.svgaplayer.SVGADrawable;
import com.opensource.svgaplayer.SVGAImageView;
import com.opensource.svgaplayer.SVGAParser;
import com.opensource.svgaplayer.SVGAVideoEntity;
import com.thfw.base.utils.LogUtil;

import org.jetbrains.annotations.NotNull;

/**
 * Author:pengs
 * Date: 2022/2/25 14:21
 * Describe:Todo
 */
public class SVGAHelper {

    private static final String TAG = SVGAHelper.class.getSimpleName();

    public static void playSVGA(SVGAImageView svgaImageView, String fileName, int loopCount, DialogRobotFactory.SimpleSVGACallBack simpleSVGACallBack) {
        SVGAParser parser = new SVGAParser(svgaImageView.getContext());
        parser.decodeFromAssets(fileName, new SVGAParser.ParseCompletion() {
            @Override
            public void onComplete(@NotNull SVGAVideoEntity svgaVideoEntity) {
                SVGADrawable drawable = new SVGADrawable(svgaVideoEntity);
                svgaImageView.setImageDrawable(drawable);
                svgaImageView.setLoops(loopCount);
//                svgaImageView.startAnimation(new SVGARange(30, svgaVideoEntity.getFrames()), false);
                svgaImageView.startAnimation();
                LogUtil.d(TAG, "startAnimation");
            }

            @Override
            public void onError() {
            }
        }, null);
        svgaImageView.setCallback(simpleSVGACallBack);
    }
}
