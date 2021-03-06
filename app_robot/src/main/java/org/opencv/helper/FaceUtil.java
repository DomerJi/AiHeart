package org.opencv.helper;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.Log;

import com.thfw.base.utils.FaceSetUtil;
import com.thfw.base.utils.LogUtil;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfFloat;
import org.opencv.core.MatOfInt;
import org.opencv.core.Rect;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by kqw on 2016/9/9.
 * FaceUtil
 */
public final class FaceUtil {

    private static final String TAG = "FaceUtil";

    private static float maxWH = 200f;

    private static int offset = FaceSetUtil.OFFSET;

    public static void setMaxWH(float maxWH) {
        if (maxWH > 100f) {
            FaceUtil.maxWH = maxWH;
        }
    }

    private FaceUtil() {
    }

    public static boolean saveImage(Context context, Mat image, Rect rect, String fileName) {
        return saveImage(context, image, rect, fileName, false);
    }

    /**
     * 特征保存灰度图
     *
     * @param context  Context
     * @param image    Mat
     * @param rect     人脸信息
     * @param fileName 文件名字
     * @return 保存是否成功
     */
    public static boolean saveImage(Context context, Mat image, Rect rect, String fileName, boolean isGray) {
        Mat grayMat = image;
        // 原图置灰
        if (!isGray) {
            grayMat = new Mat();
            Imgproc.cvtColor(image, grayMat, Imgproc.COLOR_BGR2GRAY);
        }

        Rect roi = rect;
        // 把检测到的人脸重新定义大小后保存成文件
        if (0 <= roi.x && 0 <= roi.width && roi.x + roi.width <= grayMat.cols() && 0 <= roi.y && 0 <= roi.height && roi.y + roi.height <= grayMat.rows()) {
            Mat sub = grayMat.submat(rect);

            Mat mat = new Mat();
            Size size = new Size(200, 200);
            Imgproc.resize(sub, mat, size);
            return Imgcodecs.imwrite(getFilePath(context, fileName), mat);
        }

        return false;
    }


    /**
     * 特征保存
     *
     * @param context  Context
     * @param image    Mat
     * @param fileName 文件名字
     * @return 保存是否成功
     */
    public static boolean saveImageRgba(Context context, Mat image, String fileName) {

        Mat rgbaMat = image;
        // 此参数可以控制图片尺寸，及存储大小
        int width = image.width();
        int height = image.height();
        boolean resize = false;
        if (width > maxWH) {
            float scale = maxWH / width;
            height = (int) (height * scale);
            width = (int) maxWH;
            resize = true;
        } else if (image.height() > maxWH) {
            float scale = maxWH / height;
            width = (int) (width * scale);
            height = (int) maxWH;
            resize = true;
        }

        Mat mat;
        if (resize) {
            mat = new Mat();
            Size size = new Size(width, height);
            Imgproc.resize(rgbaMat, mat, size);
        } else {
            mat = rgbaMat;
        }

        Bitmap map = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(mat, map);
        saveBitmapFile(map, getFilePath(context, fileName));
        File file = new File(getFilePath(context, fileName));
        return file != null && file.exists();
    }


    /**
     * 特征保存
     *
     * @param context  Context
     * @param image    Mat
     * @param fileName 文件名字
     * @return 保存是否成功
     */
    public static boolean saveImageRgba(Context context, Mat image, Rect rect, String fileName) {

        Mat rgbaMat = image;
        Rect roi = rect.clone();
        offset = roi.width / 3;
        LogUtil.d("roi.width = " + roi.width + " ; offset = " + offset);
        // 训练数据人脸外有一部分背景图，根据算法要求，可以设置偏移量
        // 偏移量越大人脸在图像中的比例越小，目前低于50成功率不高，需高于60
        // 把检测到的人脸重新定义大小后保存成文件
        if (0 <= roi.x && 0 <= roi.width && roi.x + roi.width <= rgbaMat.cols() && 0 <= roi.y && 0 <= roi.height && roi.y + roi.height <= rgbaMat.rows()) {
            LogUtil.d("roi.y 1= " + roi.y);
            int y = roi.y;
            if (y > offset) {
                roi.y = roi.y - offset;
            }
            LogUtil.d("roi.y 2= " + roi.y);
            if (y + roi.height < rgbaMat.height() + offset * 2) {
                roi.height = roi.height + offset * 2;
            }

            int x = roi.x;
            if (x > offset) {
                roi.x = roi.x - offset;
            }
            LogUtil.d("roi.y 2= " + roi.y);
            if (y + roi.width < rgbaMat.width() + offset * 2) {
                roi.width = roi.width + offset * 2;
            }

            if (0 <= roi.x && 0 <= roi.width && roi.x + roi.width <= rgbaMat.cols() && 0 <= roi.y && 0 <= roi.height && roi.y + roi.height <= rgbaMat.rows()) {
                Mat sub = rgbaMat.submat(roi);
                return saveImageRgba(context, sub, fileName);
            }
        }

        return saveImageRgba(context, image, fileName);
    }

    /**
     * 保存图片到文件
     *
     * @param bitmap
     * @param filePath 文件全路径
     */
    public static void saveBitmapFile(Bitmap bitmap, String filePath) {
        File file = new File(filePath);//将要保存图片的路径
        try {
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            bos.flush();
            bos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * 直方图比较两个图片是否相似，可以用来比较两个人脸图片是否是同一个人
     * <p>Title: CmpPic</p>
     * <p>Description: </p>
     *
     * @param src
     * @param des
     * @return
     * @author 陈阳
     * @date 2019年1月26日
     */
    public static double CmpPic(String src, String des) {
        Log.d(TAG, "\n==========直方图比较==========");


        //自定义阈值
        //相关性阈值，应大于多少，越接近1表示越像，最大为1
        double HISTCMP_CORREL_THRESHOLD = 0.75;
        //卡方阈值，应小于多少，越接近0表示越像
        double HISTCMP_CHISQR_THRESHOLD = 2;
        //交叉阈值，应大于多少，数值越大表示越像
        double HISTCMP_INTERSECT_THRESHOLD = 1.2;
        //巴氏距离阈值，应小于多少，越接近0表示越像
        double HISTCMP_BHATTACHARYYA_THRESHOLD = 0.3;

        try {

            long startTime = System.currentTimeMillis();
            org.opencv.core.Mat mat_src = Imgcodecs.imread(src);
            org.opencv.core.Mat mat_des = Imgcodecs.imread(des);

            if (mat_src.empty() || mat_des.empty()) {
                throw new Exception("no file.");
            }


            org.opencv.core.Mat hsv_src = new org.opencv.core.Mat();
            org.opencv.core.Mat hsv_des = new org.opencv.core.Mat();

            // 转换成HSV
            Imgproc.cvtColor(mat_src, hsv_src, Imgproc.COLOR_BGR2HSV);
            Imgproc.cvtColor(mat_des, hsv_des, Imgproc.COLOR_BGR2HSV);

            List<Mat> listImg1 = new ArrayList<Mat>();
            List<org.opencv.core.Mat> listImg2 = new ArrayList<org.opencv.core.Mat>();
            listImg1.add(hsv_src);
            listImg2.add(hsv_des);

            MatOfFloat ranges = new MatOfFloat(0, 255);
            MatOfInt histSize = new MatOfInt(50);
            MatOfInt channels = new MatOfInt(0);

            org.opencv.core.Mat histImg1 = new org.opencv.core.Mat();
            org.opencv.core.Mat histImg2 = new org.opencv.core.Mat();

            //org.bytedeco.javacpp中的方法不太了解参数，所以直接上org.opencv中的方法，所以需要加载一下dll，System.load("D:\\soft\\openCV3\\opencv\\build\\java\\x64\\opencv_java345.dll");
            //opencv_imgproc.calcHist(images, nimages, channels, mask, hist, dims, histSize, ranges, uniform, accumulate);
            Imgproc.calcHist(listImg1, channels, new org.opencv.core.Mat(), histImg1, histSize, ranges);
            Imgproc.calcHist(listImg2, channels, new org.opencv.core.Mat(), histImg2, histSize, ranges);

            org.opencv.core.Core.normalize(histImg1, histImg1, 0d, 1d, Core.NORM_MINMAX, -1,
                    new org.opencv.core.Mat());
            org.opencv.core.Core.normalize(histImg2, histImg2, 0d, 1d, Core.NORM_MINMAX, -1,
                    new org.opencv.core.Mat());

            double result0, result1, result2, result3;
            result0 = Imgproc.compareHist(histImg1, histImg2, Imgproc.HISTCMP_CORREL);
            result1 = Imgproc.compareHist(histImg1, histImg2, Imgproc.HISTCMP_CHISQR);
            result2 = Imgproc.compareHist(histImg1, histImg2, Imgproc.HISTCMP_INTERSECT);
            result3 = Imgproc.compareHist(histImg1, histImg2, Imgproc.HISTCMP_BHATTACHARYYA);

            Log.d(TAG, "相关性（度量越高，匹配越准确 [基准：" + HISTCMP_CORREL_THRESHOLD + "]）,当前值:" + result0);
            Log.d(TAG, "卡方（度量越低，匹配越准确 [基准：" + HISTCMP_CHISQR_THRESHOLD + "]）,当前值:" + result1);
            Log.d(TAG, "交叉核（度量越高，匹配越准确 [基准：" + HISTCMP_INTERSECT_THRESHOLD + "]）,当前值:" + result2);
            Log.d(TAG, "巴氏距离（度量越低，匹配越准确 [基准：" + HISTCMP_BHATTACHARYYA_THRESHOLD + "]）,当前值:" + result3);

            //一共四种方式，有三个满足阈值就算匹配成功
            int count = 0;
            if (result0 > HISTCMP_CORREL_THRESHOLD)
                count++;
            if (result1 < HISTCMP_CHISQR_THRESHOLD)
                count++;
            if (result2 > HISTCMP_INTERSECT_THRESHOLD)
                count++;
            if (result3 < HISTCMP_BHATTACHARYYA_THRESHOLD)
                count++;

            int retVal = 0;
            if (count >= 3) {
                //这是相似的图像
                retVal = 1;
            }

            long estimatedTime = System.currentTimeMillis() - startTime;

            Log.d(TAG, "花费时间= " + estimatedTime + "ms");
            Log.d(TAG, "成功与否= " + (retVal == 1));

            return retVal;
        } catch (Exception e) {
            Log.d(TAG, "例外:" + e);
        }
        return 0;
    }

    /**
     * 比较来个矩阵的相似度
     *
     * @param srcMat
     * @param desMat
     */
    public static double comPareHist(Mat srcMat, Mat desMat) {

        srcMat.convertTo(srcMat, CvType.CV_32F);
        desMat.convertTo(desMat, CvType.CV_32F);
        double target = Imgproc.compareHist(srcMat, desMat, Imgproc.CV_COMP_CORREL);
        Log.e(TAG, "相似度 ：   ==" + target);
        return target;
    }

    public static double comPareHist(String srcMat, String desMat) {
        return comPareHist(Imgcodecs.imread(srcMat), Imgcodecs.imread(desMat));
    }


    /**
     * 删除特征
     *
     * @param context  Context
     * @param fileName 特征文件
     * @return 是否删除成功
     */
    public static boolean deleteImage(Context context, String fileName) {
        // 文件名不能为空
        if (TextUtils.isEmpty(fileName)) {
            return false;
        }
        // 文件路径不能为空
        String path = getFilePath(context, fileName);
        if (path != null) {
            File file = new File(path);
            return file.exists() && file.delete();
        } else {
            return false;
        }
    }

    /**
     * 提取特征
     *
     * @param context  Context
     * @param fileName 文件名
     * @return 特征图片
     */
    public static Bitmap getImage(Context context, String fileName) {
        String filePath = getFilePath(context, fileName);
        if (TextUtils.isEmpty(filePath)) {
            return null;
        } else {
            return BitmapFactory.decodeFile(filePath);
        }
    }


    /**
     * 获取人脸特征路径
     *
     * @param fileName 人脸特征的图片的名字
     * @return 路径
     */
    public static String getFilePath(Context context, String fileName) {
        if (TextUtils.isEmpty(fileName)) {
            return null;
        }
        // 内存路径
        return context.getApplicationContext().getFilesDir().getPath() + File.separator + fileName + ".jpg";
        // 内存卡路径 需要SD卡读取权限
//        return Environment.getExternalStorageDirectory() + "/FaceDetect/" + fileName + ".jpg";
    }
}
