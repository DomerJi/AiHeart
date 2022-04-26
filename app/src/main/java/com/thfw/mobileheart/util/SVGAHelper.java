package com.thfw.mobileheart.util;

import com.opensource.svgaplayer.SVGADrawable;
import com.opensource.svgaplayer.SVGAImageView;
import com.opensource.svgaplayer.SVGAParser;
import com.opensource.svgaplayer.SVGAVideoEntity;
import com.opensource.svgaplayer.utils.SVGARange;
import com.thfw.base.utils.LogUtil;

import org.jetbrains.annotations.NotNull;

/**
 * Author:pengs
 * Date: 2022/2/25 14:21
 * Describe:Todo
 */
public class SVGAHelper {

    private static final String TAG = SVGAHelper.class.getSimpleName();

    public static void playSVGA(SVGAImageView svgaImageView, SVGAModel svgaModel, DialogFactory.SimpleSVGACallBack simpleSVGACallBack) {
        if (svgaImageView.isAnimating()) {
            svgaImageView.setCallback(null);
            svgaImageView.stopAnimation(true);
        }
        SVGAParser parser = new SVGAParser(svgaImageView.getContext());
        parser.decodeFromAssets(svgaModel.fileName, new SVGAParser.ParseCompletion() {
            @Override
            public void onComplete(@NotNull SVGAVideoEntity svgaVideoEntity) {
                SVGADrawable drawable = new SVGADrawable(svgaVideoEntity);
                svgaImageView.setImageDrawable(drawable);
                svgaImageView.setLoops(svgaModel.loopCount);
                if (svgaModel.location > 0) {
                    int length = svgaModel.length > svgaModel.location && svgaModel.length <= svgaVideoEntity.getFrames()
                            ? svgaModel.location : svgaVideoEntity.getFrames();
                    svgaImageView.startAnimation(new SVGARange(30, length), false);
                    LogUtil.d(TAG, "startAnimation(SVGARange)");
                } else {
                    svgaImageView.startAnimation();

                    LogUtil.d(TAG, "startAnimation");
                }

            }

            @Override
            public void onError() {
            }
        }, null);
        svgaImageView.setCallback(simpleSVGACallBack);
    }

    public static class SVGAModel {
        String fileName;
        // 0一直循环
        int loopCount;
        // 开始 帧
        int location;
        // 结束 帧
        int length;

        public SVGAModel(String fileName) {
            this.fileName = fileName;
        }

        public static SVGAModel create(String fileName) {
            return new SVGAModel(fileName);
        }

        public SVGAModel setFileName(String fileName) {
            this.fileName = fileName;
            return this;
        }

        public SVGAModel setLoopCount(int loopCount) {
            this.loopCount = loopCount;
            return this;
        }

        public SVGAModel setLocation(int location, int length) {
            this.location = location;
            this.length = length;
            return this;
        }
    }
}
