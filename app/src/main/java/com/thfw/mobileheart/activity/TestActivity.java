package com.thfw.mobileheart.activity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.ui.RecognizerDialog;
import com.iflytek.cloud.ui.RecognizerDialogListener;
import com.thfw.base.net.download.DownLoadIntentService;
import com.thfw.base.net.download.ProgressListener;
import com.thfw.base.net.download.TestDownLoad;
import com.thfw.base.utils.LogUtil;
import com.thfw.base.utils.ToastUtil;
import com.thfw.mobileheart.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import io.reactivex.functions.Consumer;
import io.reactivex.plugins.RxJavaPlugins;
import me.zhanghai.android.materialratingbar.MaterialRatingBar;

public class TestActivity extends AppCompatActivity {

    private static final String TAG = "TestActivity";
    private MaterialRatingBar mRatingBar;
    private ProgressBar mPbBottom;
    private Button mBtBeginDownload;
    private MaterialRatingBar ratingBar;
    private ProgressBar pbBottom;
    private Button btAiui;
    private RecognizerDialog recognizerDialog;
    /**
     * 听写UI监听器
     */
    private RecognizerDialogListener mRecognizerDialogListener = new RecognizerDialogListener() {
        public void onResult(RecognizerResult results, boolean isLast) {
            printResult(results);
        }

        /**
         * 识别回调错误.
         */
        public void onError(SpeechError error) {
            ToastUtil.show(error.getPlainDescription(true));
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        initView();
        initRatingBar();
        initRecognizerDialog();
    }

    private void initRecognizerDialog() {
        recognizerDialog = new RecognizerDialog(TestActivity.this, new InitListener() {
            @Override
            public void onInit(int i) {
                LogUtil.e("recognizerDialog - > i = " + i);
            }
        });
        // 以下为dialog设置听写参数
        recognizerDialog.setParameter(SpeechConstant.RESULT_TYPE, "json");
        // 设置语音输入语言，zh_cn为简体中文
        recognizerDialog.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
        // 设置结果返回语言
        recognizerDialog.setParameter(SpeechConstant.ACCENT, "mandarin");
        // 设置语音前端点:静音超时时间，单位ms，即用户多长时间不说话则当做超时处理
        // 取值范围{1000～10000}
        recognizerDialog.setParameter(SpeechConstant.VAD_BOS, "3000");
        // 设置语音后端点:后端点静音检测时间，单位ms，即用户停止说话多长时间内即认为不再输入，
        // 自动停止录音，范围{0~10000}
        recognizerDialog.setParameter(SpeechConstant.VAD_EOS, "8000");
        recognizerDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {

            }
        });
        recognizerDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {

            }
        });

    }

    private void initRatingBar() {
        mRatingBar.setNumStars(6);
        mRatingBar.setMax(12);
        mRatingBar.setOnRatingChangeListener(new MaterialRatingBar.OnRatingChangeListener() {
            @Override
            public void onRatingChanged(MaterialRatingBar ratingBar, float rating) {
                LogUtil.d("rating = " + rating);
                ToastUtil.show("rating = " + rating);
            }
        });

        RxJavaPlugins.setErrorHandler(new Consumer() {

            @Override
            public void accept(Object o) throws Exception {

            }
        });
    }

    private void initView() {
        mRatingBar = findViewById(R.id.ratingBar);
        mPbBottom = findViewById(R.id.pb_bottom);
        mBtBeginDownload = findViewById(R.id.bt_begin_download);

        mBtBeginDownload.setOnClickListener(v -> {
            DownLoadIntentService.setProgressListener(new ProgressListener() {
                @Override
                public void update(String url, long bytesRead, long contentLength, boolean done, String filePath) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mPbBottom.setProgress((int) (bytesRead * 1000 / contentLength));
                        }
                    });

                }
            });
            DownLoadIntentService.begin(getBaseContext(), TestDownLoad.testUrls[0]);
        });

        // 语音测试
        btAiui = (Button) findViewById(R.id.bt_aiui);
        btAiui.setOnClickListener(v -> {
            recognizerDialog.setListener(mRecognizerDialogListener);
            recognizerDialog.show();
        });

    }

    /**
     * sn	sentence	number	第几句
     * ls	last sentence	boolean	是否最后一句
     * bg	begin	number	保留字段，无需关注
     * ed	end	number	保留字段，无需关注
     * ws	words	array	词
     * cw	chinese word	array	中文分词
     * w	word	string	单字
     * sc	score	number	分数
     *
     * @param results
     */
    private void printResult(RecognizerResult results) {
        LogUtil.e("printResult->" + results.getResultString());
        String sn = null;
        // 读取json结果中的sn字段
        try {
            JSONObject resultJson = new JSONObject(results.getResultString());
            sn = resultJson.optString("sn");
            JSONArray jsonArray = resultJson.optJSONArray("ws");
            if (jsonArray != null) {
                StringBuffer stringBuffer = new StringBuffer();
                int length = jsonArray.length();
                for (int i = 0; i < length; i++) {
                    JSONObject jsonObject = jsonArray.optJSONObject(i);
                    if (jsonObject != null) {
                        JSONArray cwArray = jsonObject.optJSONArray("cw");
                        if (cwArray != null && cwArray.length() > 0) {
                            JSONObject object = cwArray.optJSONObject(0);
                            if (object != null) {
                                String words = object.optString("w");
                                stringBuffer.append(words);
                            }
                        }
                    }
                }
                ToastUtil.show(stringBuffer.toString());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}