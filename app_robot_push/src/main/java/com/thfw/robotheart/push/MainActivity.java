package com.thfw.robotheart.push;

import android.os.Bundle;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = 0f;
        getWindow().setAttributes(lp);
        MyApplication.agreementAfterInitUmeng();
        finish();
        overridePendingTransition(0, 0);
    }
}