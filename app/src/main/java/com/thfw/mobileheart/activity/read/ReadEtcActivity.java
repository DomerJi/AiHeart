package com.thfw.mobileheart.activity.read;

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
import com.thfw.mobileheart.adapter.ReadEtcAdapter;
import com.thfw.base.models.ReadEtcModel;
import com.thfw.mobileheart.R;
import com.thfw.ui.base.BaseActivity;
import com.thfw.mobileheart.util.DialogFactory;
import com.thfw.ui.dialog.TDialog;
import com.thfw.ui.dialog.base.BindViewHolder;
import com.thfw.ui.dialog.listener.OnBindViewListener;
import com.thfw.ui.dialog.listener.OnViewClickListener;
import com.thfw.ui.widget.TitleView;

import java.util.List;

@Deprecated
public class ReadEtcActivity extends BaseActivity {

    private TitleView mTitleView;
    private ImageView mIvShare;
    private RelativeLayout mRlBanner;
    private RoundedImageView mRivEtcBanner;
    private TextView mTvTitle;
    private TextView mTvHint;
    private LinearLayout mLlPlay;
    private SmartRefreshLayout mRefreshLayout;
    private RecyclerView mRvList;

    @Override
    public int getContentView() {
        return R.layout.activity_read_etc;
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
            DialogFactory.createShare(ReadEtcActivity.this, new OnBindViewListener() {
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
        ReadEtcAdapter readEtcAdapter = new ReadEtcAdapter(null);
        readEtcAdapter.setOnRvItemListener(new OnRvItemListener<ReadEtcModel>() {
            @Override
            public void onItemClick(List<ReadEtcModel> list, int position) {
                startActivity(new Intent(mContext, ReadDetailsActivity.class));
            }
        });
        mRvList.setAdapter(readEtcAdapter);
    }
}