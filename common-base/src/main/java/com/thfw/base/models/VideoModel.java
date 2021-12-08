package com.thfw.base.models;

import com.thfw.base.base.IModel;

import java.util.ArrayList;
import java.util.List;

public class VideoModel implements IModel {

    private static String videoUrl01 = "http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4";
    private static String videoUrl02 = "http://gslb.miaopai.com/stream/oxX3t3Vm5XPHKUeTS-zbXA__.mp4";
    private static String videoUrl03 = "http://flv2.bn.netease.com/videolib3/1611/28/nNTov5571/SD/nNTov5571-mobile.mp4";


    public String url;
    public String title;

    public static List<VideoModel> getVideoUrl() {
        List<VideoModel> videoModels = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            VideoModel videoModel = new VideoModel();
            switch (i % 3) {
                case 0:
                    videoModel.title = "小兔子";
                    videoModel.url = videoUrl01;
                    break;
                case 1:
                    videoModel.title = "马云演讲";
                    videoModel.url = videoUrl02;
                    break;
                default:
                    videoModel.title = "海洋";
                    videoModel.url = videoUrl03;
                    break;
            }

            videoModels.add(videoModel);
        }
        return videoModels;

    }
}
