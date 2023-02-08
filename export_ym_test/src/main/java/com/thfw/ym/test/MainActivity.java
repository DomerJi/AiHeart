package com.thfw.ym.test;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.thfw.export_ym.YmHandler;
import com.thfw.util.LogUtil;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        LogUtil.setLogEnabled(true);
        /**
         *  【必须】设置appKEY todo 放在Application onCreate方法中
         */
        // 50  莲花街道
//        YmHandler.setAppKey("b2ec1a50655c39119588c8e6b14bac54");
        // 184
        YmHandler.setAppKey("b7228c398b65526ffcfc44c75a2304e8");
        String phone = "18612344322";
        // 2 天和云脉
//        YmHandler.setAppKey("dd9158215a783d2d8405257b0a7aa06c");


        /**
         * 开始测评
         */
        findViewById(R.id.bt_go_testing).setOnClickListener(v -> {
            // 用户id,手机号,用户名
            YmHandler.startMentalTest(MainActivity.this, "100322", phone, "DomerJi");
        });

        /**
         * 测评报告
         */
        findViewById(R.id.bt_test_port).setOnClickListener(v -> {
            // 用户id,手机号,用户名
            YmHandler.startMentalTestPortList(MainActivity.this, "100322", phone, "DomerJi");
        });


        /**
         *  【非必须】监听登录是否成功
         */
        YmHandler.setOnYmLoginCallBack(new YmHandler.OnYmLoginCallBack() {
            @Override
            public void onSuccess() {
                Log.i("YmHandler", "OnYmLoginCallBack -> onSuccess");
            }

            @Override
            public void onFail(int i, String s) {
                Log.e("YmHandler", "OnYmLoginCallBack -> onFail( " + i + " , " + s + " )");
            }
        });

        /**
         * 【非必须】 设置第一次登录时的延时loading框
         */

        YmHandler.setOnYmLoginLoadingListener(new YmHandler.OnYmLoginLoadingListener() {
            @Override
            public void show() {
                Log.i("YmHandler", "loading .... show");
            }

            @Override
            public void hide() {
                Log.i("YmHandler", "loading .... hide");
            }
        });


    }
}