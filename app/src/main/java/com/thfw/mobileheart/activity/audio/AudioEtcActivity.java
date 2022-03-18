package com.thfw.mobileheart.activity.audio;

import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.makeramen.roundedimageview.RoundedImageView;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.thfw.base.base.IPresenter;
import com.thfw.base.face.OnRvItemListener;
import com.thfw.base.models.AudioEtcModel;
import com.thfw.mobileheart.R;
import com.thfw.mobileheart.adapter.AudioEtcAdapter;
import com.thfw.ui.base.BaseActivity;
import com.thfw.mobileheart.util.DialogFactory;
import com.thfw.ui.dialog.TDialog;
import com.thfw.ui.dialog.base.BindViewHolder;
import com.thfw.ui.dialog.listener.OnBindViewListener;
import com.thfw.ui.dialog.listener.OnViewClickListener;
import com.thfw.ui.widget.TitleView;

import java.util.List;

/**
 * 废弃 无详情页，直接播放
 */
@Deprecated
public class AudioEtcActivity extends BaseActivity {

    private com.thfw.ui.widget.TitleView mTitleView;
    private android.widget.ImageView mIvShare;
    private android.widget.RelativeLayout mRlBanner;
    private com.makeramen.roundedimageview.RoundedImageView mRivEtcBanner;
    private android.widget.TextView mTvTitle;
    private android.widget.TextView mTvHint;
    private android.widget.LinearLayout mLlPlay;
    private com.scwang.smart.refresh.layout.SmartRefreshLayout mRefreshLayout;
    private androidx.recyclerview.widget.RecyclerView mRvList;

    @Override
    public int getContentView() {
        return R.layout.activity_audio_etc;
    }

    @Override
    public IPresenter onCreatePresenter() {
        return null;
    }

    @Override
    public void initView() {

        mTitleView = (TitleView) findViewById(R.id.titleView);
        mIvShare = (ImageView) findViewById(R.id.iv_share);
        mRlBanner = (RelativeLayout) findViewById(R.id.rl_banner);
        mRivEtcBanner = (RoundedImageView) findViewById(R.id.riv_etc_banner);
        mTvTitle = (TextView) findViewById(R.id.tv_title);
        mTvHint = (TextView) findViewById(R.id.tv_hint);
        mLlPlay = (LinearLayout) findViewById(R.id.ll_play);
        mRefreshLayout = (SmartRefreshLayout) findViewById(R.id.refreshLayout);
        mRvList = (RecyclerView) findViewById(R.id.rv_list);
        mIvShare.setOnClickListener(v -> {
            DialogFactory.createShare(AudioEtcActivity.this, new OnBindViewListener() {
                @Override
                public void bindView(BindViewHolder viewHolder) {

                }
            }, new OnViewClickListener() {
                @Override
                public void onViewClick(BindViewHolder viewHolder, View view, TDialog tDialog) {

                }
            });
        });
    }

    @Override
    public void initData() {
        mRvList.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        AudioEtcAdapter audioEtcAdapter = new AudioEtcAdapter(null);
        audioEtcAdapter.setOnRvItemListener(new OnRvItemListener<AudioEtcModel>() {
            @Override
            public void onItemClick(List<AudioEtcModel> list, int position) {
                startActivity(new Intent(mContext, AudioPlayerActivity.class));
            }
        });
        mRvList.setAdapter(audioEtcAdapter);
    }
}