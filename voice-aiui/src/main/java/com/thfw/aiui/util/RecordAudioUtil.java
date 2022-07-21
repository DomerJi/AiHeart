package com.thfw.aiui.util;

/**
 * 录音数据转换工具类
 *  16bit 转 32bit
 *  通道数转换
 */
public class RecordAudioUtil {

    // 六通道边四通道，采样精度32bit
    public static byte[] resetAudio(byte[] data){
        if (data==null) return null;
        int datalength=data.length;
        int size=datalength*4/6;
        byte[] newdata=new byte[size];
        int count=datalength/6/4;
        for (int i=0;i<count;i++){
            System.arraycopy(data, 6*i*4, newdata, 4*i*4, 4);
            System.arraycopy(data, (6*i+3)*4, newdata, (4*i+1)*4, 4);
            System.arraycopy(data, (6*i+4)*4,newdata, (4*i+2)*4, 8);
        }
        return newdata;
    }

    //单麦通道适配 16bit转32bit
    public static byte[] addCnFor1Mic(byte[] data) {
        byte[] cpy=new byte[data.length*2];
        int j=0;

        //通道： mic ref
        while(j<data.length/4) {
            cpy[8*j]=00;
            cpy[8*j+1]=  00;
            cpy[8 * j + 2] = data[4 * j +0];
            cpy[8* j + 3] = data[4 * j +1];

            cpy[8*j+4]=00;
            cpy[8*j+5]=  00;
            cpy[8 * j + 6] = data[4 * j +2];
            cpy[8* j + 7] = data[4 * j +3];

            j++;
        }
        return cpy;
    }

    //2mic通道适配 16k 16bit 转 16k 32bit
    public static byte[] addCnFor2Mic(byte[] data) {
        byte[] cpy=new byte[data.length*2];
        int j=0;

        //通道： mic1 mic2 ref ref
        while(j<data.length/8) {
            cpy[16*j]=00;
            cpy[16*j+1]=  00;
            cpy[16 * j + 2] = data[8 * j +0];
            cpy[16* j + 3] = data[8 * j +1];

            cpy[16*j+4]=00;
            cpy[16*j+5]=  00;
            cpy[16 * j + 6] = data[8 * j +2];
            cpy[16* j + 7] = data[8 * j +3];

            cpy[16*j+8]=00;
            cpy[16*j+9]=  00;
            cpy[16 * j + 10] = data[8 * j +4];
            cpy[16* j + 11] = data[8 * j +5];

            cpy[16*j+12]=00;
            cpy[16*j+13]=  00;
            cpy[16 * j + 14] = data[8 * j +6];
            cpy[16* j + 15] = data[8 * j +7];

            j++;
        }
        return cpy;
    }

    //2mic通道适配 16bit 转 32bit
    //回采信号置空
    public static byte[] addCnFor2MicN1(byte[] data) {
        byte[] cpy=new byte[data.length*4];
        int j=0;

        //通道： mic1 mic2 ref ref
        while(j<data.length/4) {
            cpy[16*j]=00;
            cpy[16*j+1]=  00;
            cpy[16 * j + 2] = data[4 * j +0];
            cpy[16* j + 3] = data[4 * j +1];

            cpy[16*j+4]=00;
            cpy[16*j+5]=  00;
            cpy[16 * j + 6] = data[4 * j +2];
            cpy[16* j + 7] = data[4 * j +3];

            cpy[16*j+8]=00;
            cpy[16*j+9]=  00;
            cpy[16 * j + 10] = 00;
            cpy[16* j + 11] = 00;

            cpy[16*j+12]=00;
            cpy[16*j+13]=  00;
            cpy[16 * j + 14] = 00;
            cpy[16* j + 15] = 00;

            j++;
        }
        return cpy;
    }

    //2mic通道适配 48k 16bit 转 16k 32bit
    public static byte[] addCnFor2MicN2(byte[] data) {
        byte[] cpy=new byte[data.length*2/3];
        int j=0;

        //通道： mic1 mic2 ref ref
        while(j<data.length/24) {
            cpy[16*j]=00;
            cpy[16*j+1]=  00;
            cpy[16 * j + 2] = data[24 * j +6];
            cpy[16* j + 3] = data[24 * j +7];

            cpy[16*j+4]=00;
            cpy[16*j+5]=  00;
            cpy[16 * j + 6] = data[24 * j +2];
            cpy[16* j + 7] = data[24 * j +3];

            cpy[16*j+8]=00;
            cpy[16*j+9]=  00;
            cpy[16 * j + 10] = data[24 * j +4];
            cpy[16* j + 11] = data[24 * j +5];

            cpy[16*j+12]=00;
            cpy[16*j+13]=  00;
            cpy[16 * j + 14] = data[24 * j +0];
            cpy[16* j + 15] = data[24 * j +1];
            j++;
        }
        return cpy;
    }

    //4mic通道适配 16bit 转 32bit
    public static byte[] addCnFor4Mic(byte[] data) {
        byte[] cpy=new byte[data.length*2];
        int j=0;

        //通道： mic1 mic2 mic3 mic4 ref ref
        while(j<data.length/12) {
            cpy[24*j]=00;
            cpy[24*j+1]=  00;
            cpy[24 * j + 2] = data[12 * j +0];
            cpy[24* j + 3] = data[12 * j +1];

            cpy[24*j+4]=00;
            cpy[24*j+5]=  00;
            cpy[24 * j + 6] = data[12 * j +2];
            cpy[24* j + 7] = data[12 * j +3];

            cpy[24*j+8]=00;
            cpy[24*j+9]=  00;
            cpy[24 * j + 10] = data[12 * j +4];
            cpy[24* j + 11] = data[12 * j +5];

            cpy[24*j+12]=00;
            cpy[24*j+13]=  00;
            cpy[24 * j + 14] = data[12 * j +6];
            cpy[24* j + 15] = data[12 * j +7];

            cpy[24*j+16]=00;
            cpy[24*j+17]=  00;
            cpy[24 * j + 18] = data[12 * j +8];
            cpy[24* j + 19] = data[12 * j +9];

            cpy[24*j+20]=00;
            cpy[24*j+21]=  00;
            cpy[24 * j + 22] = data[12 * j +10];
            cpy[24* j + 23] = data[12 * j +11];

            j++;
        }
        return cpy;
    }

    //2mic（2通道 96k 16bit）转 4通道 16k 32bit
    public static byte[] parse2mic(byte[] data) {
        byte[] cpy=new byte[data.length/12*4*2];
        int j=0;

        //通道： mic1 mic2 mic3 mic4 ref ref
        while(j<data.length/24) {
            cpy[16*j] = 00;
            cpy[16*j + 1] = 00;
            cpy[16*j + 2] = data[24 * j +0];
            cpy[16*j + 3] = data[24 * j +1];

            cpy[16*j + 4] = 00;
            cpy[16*j + 5] = 00;
            cpy[16*j + 6] = data[24 * j +2];
            cpy[16*j + 7] = data[24 * j +3];

            cpy[16*j + 8] = 00;
            cpy[16*j + 9] = 00;
            cpy[16*j + 10] = 00;
            cpy[16*j + 11] = 00;

            cpy[16*j + 12] = 00;
            cpy[16*j + 13] = 00;
            cpy[16*j + 14] = 00;
            cpy[16*j + 15] = 00;

            j++;
        }
        return cpy;
    }

    // usb录音小板, 8通道数据转4通道（2mic）、16bit转32bit
    public static byte[] adapeter2Mic(byte[] data) {
        byte[] cpy=new byte[data.length];
        int j=0;

        //通道： mic1 mic2 ref ref
        while(j<data.length/16) {
            cpy[16*j]=00;
            cpy[16*j+1]=  (byte)1;
            cpy[16 * j + 2] = data[16 * j +0];
            cpy[16* j + 3] = data[16 * j +1];

            cpy[16*j+4]=00;
            cpy[16*j+5]=  (byte)2;
            cpy[16 * j + 6] = data[16 * j +2];
            cpy[16* j + 7] = data[16 * j +3];

            cpy[16*j+8]=00;
            cpy[16*j+9]=  (byte)3;
            cpy[16 * j + 10] = data[16 * j +12];
            cpy[16* j + 11] = data[16 * j +13];

            cpy[16*j+12]=00;
            cpy[16*j+13]=  (byte)4;
            cpy[16 * j + 14] = data[16 * j +14];
            cpy[16* j + 15] = data[16 * j +15];

            j++;
        }
        return cpy;
    }

    // usb录音小板, 8通道数据转6通道（4mic）、16bit转32bit
    public static byte[] adapeter4Mic(byte[] data) {
        int size = (data.length/8)*6*2;
        byte[] cpy=new byte[size];
        int j=0;

        while(j<data.length/16) {
            cpy[24 * j + 0] = 0x00;
            cpy[24* j + 1] = 0x01;
            cpy[24 * j + 2] = data[16 * j +0];
            cpy[24* j + 3] = data[16 * j +1];

            cpy[24 * j + 4] = 0x00;
            cpy[24* j + 5] = 0x02;
            cpy[24 * j + 6] = data[16 * j +2];
            cpy[24* j + 7] = data[16 * j +3];

            cpy[24 * j + 8] = 0x00;
            cpy[24* j + 9] = 0x03;
            cpy[24 * j + 10] = data[16 * j +4];
            cpy[24* j + 11] = data[16 * j +5];

            cpy[24 * j + 12] = 0x00;
            cpy[24* j + 13] = 0x04;
            cpy[24 * j + 14] = data[16 * j +6];
            cpy[24* j + 15] = data[16 * j +7];

            //通道7--》ref1
            cpy[24 * j + 16] = 0x00;
            cpy[24* j + 17] = 0x05;
            cpy[24 * j + 18] = data[16 * j +12];
            cpy[24* j + 19] = data[16 * j +13];

            //通道8 --》 ref2
            cpy[24 * j + 20] = 0x00;
            cpy[24* j + 21] = 0x06;
            cpy[24 * j + 22] = data[16 * j +14];
            cpy[24* j + 23] = data[16 * j +15];

            j++;
        }
        return cpy;
    }

    // 环形6Mic录音板，16bit 转 32bit
    public static byte[] adapeter6Mic(byte[] data) {
        int size = data.length*2;
        byte[] cpy=new byte[size];
        int j=0;

        while(j<data.length/16) {
            cpy[32 * j + 0] = 0x00;
            cpy[32* j + 1] = 0x01;
            cpy[32 * j + 2] = data[16 * j +0];
            cpy[32* j + 3] = data[16 * j +1];

            cpy[32 * j + 4] = 0x00;
            cpy[32* j + 5] = 0x02;
            cpy[32 * j + 6] = data[16 * j +2];
            cpy[32* j + 7] = data[16 * j +3];

            cpy[32 * j + 8] = 0x00;
            cpy[32* j + 9] = 0x03;
            cpy[32 * j + 10] = data[16 * j +4];
            cpy[32* j + 11] = data[16 * j +5];

            cpy[32 * j + 12] = 0x00;
            cpy[32* j + 13] = 0x04;
            cpy[32 * j + 14] = data[16 * j +6];
            cpy[32* j + 15] = data[16 * j +7];

            cpy[32 * j + 16] = 0x00;
            cpy[32* j + 17] = 0x05;
            cpy[32 * j + 18] = data[16 * j +8];
            cpy[32* j + 19] = data[16 * j +9];

            cpy[32 * j + 20] = 0x00;
            cpy[32* j + 21] = 0x06;
            cpy[32 * j + 22] = data[16 * j +10];
            cpy[32* j + 23] = data[16 * j +11];

            //通道7--》ref1
            cpy[32 * j + 24] = 0x00;
            cpy[32* j + 25] = 0x07;
            cpy[32 * j + 26] = data[16 * j +12];
            cpy[32* j + 27] = data[16 * j +13];

            //通道8 --》 ref2
            cpy[32 * j + 28] = 0x00;
            cpy[32* j + 29] = 0x08;
            cpy[32 * j + 30] = data[16 * j +14];
            cpy[32* j + 31] = data[16 * j +15];

            j++;
        }
        return cpy;
    }


    public static int mic1 = 8;
    public static int mic2 = 2;
    public static int mic3 = 7;
    public static int mic4 = 1;
    public static int mic5 = 9;
    public static int mic6 = 3;
    public static int ref1 = 10;
    public static int ref2 = 4;
    // 使用与录音小板及环形6mic阵列，
    // 32bit、16k、12通道音频 转 8通道
    public static byte[] changer12to8(byte[] bytes){
        int index=0;
        // 查找第一的通道号
        for (index=0;index< 48; index=index+4){
            if ((bytes[index+1]&0x0f)==1){
                break;
            }
        }
        int length=bytes.length;
        int newlength=(((length-index)/48)*48)*8/12;
        byte[] data=new byte[newlength];
        int size=(length-index)/48;
        for (int i=0;i<size;i++){
            // 拷贝每帧数据
            System.arraycopy(bytes, index+(mic1-1)*4+48*i, data, 32*i, 4);
            System.arraycopy(bytes, index+(mic2-1)*4+48*i, data, 4+32*i, 4);
            System.arraycopy(bytes, index+(mic3-1)*4+48*i, data, 8+32*i, 4);
            System.arraycopy(bytes, index+(mic4-1)*4+48*i, data, 12+32*i, 4);
            System.arraycopy(bytes, index+(mic5-1)*4+48*i, data, 16+32*i, 4);
            System.arraycopy(bytes, index+(mic6-1)*4+48*i, data, 20+32*i, 4);
            System.arraycopy(bytes, index+(ref1-1)*4+48*i, data, 24+32*i, 4);
            System.arraycopy(bytes, index+(ref2-1)*4+48*i, data, 28+32*i, 4);

            // 去除通道号
            data[32*i+1]=(byte)(data[32*i+1]&0xf0);
            data[32*i+5]=(byte)(data[32*i+5]&0xf0);
            data[32*i+9]=(byte)(data[32*i+9]&0xf0);
            data[32*i+13]=(byte)(data[32*i+13]&0xf0);
            data[32*i+17]=(byte)(data[32*i+17]&0xf0);
            data[32*i+21]=(byte)(data[32*i+21]&0xf0);
            data[32*i+25]=(byte)(data[32*i+25]&0xf0);
            data[32*i+29]=(byte)(data[32*i+29]&0xf0);
        }
        return data;
    }

}


