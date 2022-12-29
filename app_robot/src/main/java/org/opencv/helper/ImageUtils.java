package org.opencv.helper;

import android.graphics.Bitmap;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.media.Image;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;

public class ImageUtils {

    private static final String TAG = "ImageUtils";

    public static byte[] imageToByteArray(Image image) {
        byte[] data = null;
        if (image.getFormat() == ImageFormat.JPEG) {
            Image.Plane[] planes = image.getPlanes();
            ByteBuffer buffer = planes[0].getBuffer();
            data = new byte[buffer.capacity()];
            buffer.get(data);
            return data;
        } else if (image.getFormat() == ImageFormat.YUV_420_888) {
            data = NV21toJPEG(YUV_420_888toNV21(image), image.getWidth(), image.getHeight());
        }
        return data;
    }

    public static byte[] YUV_420_888toNV21(Image image) {
        Rect crop = image.getCropRect();
        int format = image.getFormat();
        int width = crop.width();
        int height = crop.height();
        Image.Plane[] planes = image.getPlanes();
        byte[] data = new byte[width * height * ImageFormat.getBitsPerPixel(format) / 8];
        byte[] rowData = new byte[planes[0].getRowStride()];
        int channelOffset = 0;
        int outputStride = 1;
        for (int i = 0; i < planes.length; i++) {
            switch (i) {
                case 0:
                    channelOffset = 0;
                    outputStride = 1;
                    break;
                case 1:
                    channelOffset = width * height + 1;
                    outputStride = 2;
                    break;
                case 2:
                    channelOffset = width * height;
                    outputStride = 2;
                    break;
            }
            ByteBuffer buffer = planes[i].getBuffer();
            int rowStride = planes[i].getRowStride();
            int pixelStride = planes[i].getPixelStride();

            int shift = (i == 0) ? 0 : 1;
            int w = width >> shift;
            int h = height >> shift;
            buffer.position(rowStride * (crop.top >> shift) + pixelStride * (crop.left >> shift));
            for (int row = 0; row < h; row++) {
                int length;
                if (pixelStride == 1 && outputStride == 1) {
                    length = w;
                    buffer.get(data, channelOffset, length);
                    channelOffset += length;
                } else {
                    length = (w - 1) * pixelStride + 1;
                    buffer.get(rowData, 0, length);
                    for (int col = 0; col < w; col++) {
                        data[channelOffset] = rowData[col * pixelStride];
                        channelOffset += outputStride;
                    }
                }
                if (row < h - 1) {
                    buffer.position(buffer.position() + rowStride - length);
                }
            }
        }
        return data;
    }

    public static byte[] NV21toJPEG(byte[] nv21, int width, int height) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        YuvImage yuv = new YuvImage(nv21, ImageFormat.NV21, width, height, null);
        yuv.compressToJpeg(new Rect(0, 0, width, height), 100, out);
        return out.toByteArray();
    }

    private static int[] pixelsTmp;

    public static int[] convertNv21toArgb8888(final byte[] data, final int width, final int height) {
        final int size = width * height;
        if (pixelsTmp == null) {
            pixelsTmp = new int[size];
        }
        int u;
        int v;
        int y1;
        int y2;
        int y3;
        int y4;

        for (int i = 0, k = 0; i < size; i += 2, k += 2) {
            y1 = data[i] & 0xff;
            y2 = data[i + 1] & 0xff;
            y3 = data[width + i] & 0xff;
            y4 = data[width + i + 1] & 0xff;

            v = data[size + k] & 0xff;
            u = data[size + k + 1] & 0xff;
            v = v - 128;
            u = u - 128;

            pixelsTmp[i] = convertYuvToArgb(y1, u, v);
            pixelsTmp[i + 1] = convertYuvToArgb(y2, u, v);
            pixelsTmp[width + i] = convertYuvToArgb(y3, u, v);
            pixelsTmp[width + i + 1] = convertYuvToArgb(y4, u, v);

            if (i != 0 && (i + 2) % width == 0) {
                i += width;
            }
        }

        return pixelsTmp;
    }

    @SuppressWarnings("PMD.ShortVariable")
    private static int convertYuvToArgb(final int y, final int u, final int v) {
        int r = y + (int) (1.772f * v);
        int g = y - (int) (0.344f * v + 0.714f * u);
        int b = y + (int) (1.402f * u);
        r = r > 255 ? 255 : r < 0 ? 0 : r;
        g = g > 255 ? 255 : g < 0 ? 0 : g;
        b = b > 255 ? 255 : b < 0 ? 0 : b;
        return 0xff000000 | (r << 16) | (g << 8) | b;
    }

    public static Bitmap getBitmapFromBytes(final int[] data, int previewWidth, int previewHeight) {
        final Bitmap bitmap = Bitmap.createBitmap(previewWidth, previewHeight, Bitmap.Config.ARGB_8888);
        bitmap.setPixels(data, 0, previewWidth, 0, 0, previewWidth, previewHeight);

        return bitmap;
    }

    public static byte[] getByteArrayFromBitmap(final Bitmap bitmap) {
        final int byteCount = bitmap.getByteCount();
        final ByteBuffer byteBuffer = ByteBuffer.allocate(byteCount);
        bitmap.copyPixelsToBuffer(byteBuffer);

        return byteBuffer.array();
    }


}