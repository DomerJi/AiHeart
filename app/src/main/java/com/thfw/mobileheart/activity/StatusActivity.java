package com.thfw.mobileheart.activity;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.thfw.base.base.IPresenter;
import com.thfw.base.utils.BitmapUtil;
import com.thfw.base.utils.LogUtil;
import com.thfw.base.utils.PaletteUtil;
import com.thfw.mobileheart.adapter.StatusAdapter;
import com.thfw.mobileheart.model.StatusEntity;
import com.thfw.robotheart.R;
import com.thfw.ui.base.BaseActivity;
import com.thfw.ui.widget.LinearTopLayout;
import com.thfw.ui.widget.TitleView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * 我的状态
 */
public class StatusActivity extends BaseActivity {

    private com.thfw.ui.widget.TitleView mTitleView;
    private androidx.recyclerview.widget.RecyclerView mRvStatusList;
    private StatusAdapter mStatusAdapter;
    private android.widget.ImageView mIvBlurBg;

    // 上滑渐变参数
    private int ivHeight;
    private int topHeight;
    private int maxHeight;
    private int minHeight;
    private com.thfw.ui.widget.LinearTopLayout mLtlTop;

    @Override
    public int getContentView() {
        return R.layout.activity_status;
    }

    @Override
    public IPresenter onCreatePresenter() {
        return null;
    }

    @Override
    public int getStatusBarColor() {
        return STATUSBAR_TRANSPARENT;
    }

    @Override
    public void initView() {

        mTitleView = (TitleView) findViewById(R.id.titleView);
        mRvStatusList = (RecyclerView) findViewById(R.id.rv_status_list);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(mContext, 3);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return mStatusAdapter.getDataList().get(position).type == StatusEntity.TYPE_BODY ? 1 : 3;
            }
        });
        mRvStatusList.setLayoutManager(gridLayoutManager);
        mStatusAdapter = new StatusAdapter(getList());
        mRvStatusList.setAdapter(mStatusAdapter);


        mIvBlurBg = (ImageView) findViewById(R.id.iv_blur_bg);
        Bitmap bitmap = BitmapUtil.getResourceBitmap(mContext, R.mipmap.cat);
        mIvBlurBg.setImageBitmap(PaletteUtil.doBlur(bitmap, 30, true));
        mRvStatusList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull @NotNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });
        mRvStatusList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            int dyAll = 0;

            @Override
            public void onScrolled(@NonNull @NotNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                dyAll += dy;
                onScroll(dyAll);
            }
        });
        mLtlTop = (LinearTopLayout) findViewById(R.id.ltl_top);
    }

    /**
     * 顶部搜索导航区域背景渐变
     *
     * @param y
     */
    public void onScroll(int y) {
        LogUtil.d("onScroll(y)" + y);
        if (ivHeight == 0 || topHeight == 0) {
            ivHeight = mStatusAdapter.getTopBanner();
            topHeight = mLtlTop.getMeasuredHeight();
            maxHeight = ivHeight - topHeight;
            minHeight = maxHeight - topHeight;
            return;
        }
        // LogUtil.d("ivHeight = " + ivHeight + ";topHeight = " + topHeight);

        if (y > maxHeight) {
            mLtlTop.setBackgroundColor(Color.argb(255, 89, 198, 193));
        } else if (y < minHeight) {
            mLtlTop.setBackgroundColor(Color.TRANSPARENT);
        } else {
            float rate = (y - minHeight) * 1.0f / topHeight;
            int a = (int) (rate * 255);
            mLtlTop.setBackgroundColor(Color.argb(a, 89, 198, 193));
        }
    }

    public List<StatusEntity> getList() {
        List<StatusEntity> arrayList = new ArrayList<>();
        arrayList.add(new StatusEntity().setType(StatusEntity.TYPE_TOP));
        arrayList.add(new StatusEntity().setType(StatusEntity.TYPE_GROUP));
        arrayList.add(new StatusEntity().setType(StatusEntity.TYPE_BODY));
        arrayList.add(new StatusEntity().setType(StatusEntity.TYPE_BODY));
        arrayList.add(new StatusEntity().setType(StatusEntity.TYPE_BODY));
        arrayList.add(new StatusEntity().setType(StatusEntity.TYPE_BODY));
        arrayList.add(new StatusEntity().setType(StatusEntity.TYPE_GROUP));
        arrayList.add(new StatusEntity().setType(StatusEntity.TYPE_BODY));
        arrayList.add(new StatusEntity().setType(StatusEntity.TYPE_BODY));
        arrayList.add(new StatusEntity().setType(StatusEntity.TYPE_BODY));
        arrayList.add(new StatusEntity().setType(StatusEntity.TYPE_BODY));
        arrayList.add(new StatusEntity().setType(StatusEntity.TYPE_GROUP));
        arrayList.add(new StatusEntity().setType(StatusEntity.TYPE_BODY));
        arrayList.add(new StatusEntity().setType(StatusEntity.TYPE_BODY));
        arrayList.add(new StatusEntity().setType(StatusEntity.TYPE_BODY));
        arrayList.add(new StatusEntity().setType(StatusEntity.TYPE_BODY));
        arrayList.add(new StatusEntity().setType(StatusEntity.TYPE_BODY));
        arrayList.add(new StatusEntity().setType(StatusEntity.TYPE_GROUP));
        arrayList.add(new StatusEntity().setType(StatusEntity.TYPE_BODY));
        arrayList.add(new StatusEntity().setType(StatusEntity.TYPE_BODY));
        arrayList.add(new StatusEntity().setType(StatusEntity.TYPE_BODY));
        arrayList.add(new StatusEntity().setType(StatusEntity.TYPE_BODY));
        arrayList.add(new StatusEntity().setType(StatusEntity.TYPE_GROUP));
        arrayList.add(new StatusEntity().setType(StatusEntity.TYPE_BODY));
        arrayList.add(new StatusEntity().setType(StatusEntity.TYPE_BODY));
        arrayList.add(new StatusEntity().setType(StatusEntity.TYPE_BODY));
        arrayList.add(new StatusEntity().setType(StatusEntity.TYPE_BODY));
        arrayList.add(new StatusEntity().setType(StatusEntity.TYPE_GROUP));
        arrayList.add(new StatusEntity().setType(StatusEntity.TYPE_BODY));
        arrayList.add(new StatusEntity().setType(StatusEntity.TYPE_BODY));
        arrayList.add(new StatusEntity().setType(StatusEntity.TYPE_BODY));
        arrayList.add(new StatusEntity().setType(StatusEntity.TYPE_BODY));
        arrayList.add(new StatusEntity().setType(StatusEntity.TYPE_GROUP));
        arrayList.add(new StatusEntity().setType(StatusEntity.TYPE_BODY));
        arrayList.add(new StatusEntity().setType(StatusEntity.TYPE_BODY));
        arrayList.add(new StatusEntity().setType(StatusEntity.TYPE_BODY));
        arrayList.add(new StatusEntity().setType(StatusEntity.TYPE_BODY));
        arrayList.add(new StatusEntity().setType(StatusEntity.TYPE_BODY));
        arrayList.add(new StatusEntity().setType(StatusEntity.TYPE_GROUP));
        arrayList.add(new StatusEntity().setType(StatusEntity.TYPE_BODY));
        arrayList.add(new StatusEntity().setType(StatusEntity.TYPE_BODY));
        arrayList.add(new StatusEntity().setType(StatusEntity.TYPE_BODY));
        arrayList.add(new StatusEntity().setType(StatusEntity.TYPE_BODY));
        arrayList.add(new StatusEntity().setType(StatusEntity.TYPE_BODY));
        arrayList.add(new StatusEntity().setType(StatusEntity.TYPE_GROUP));
        arrayList.add(new StatusEntity().setType(StatusEntity.TYPE_BODY));
        arrayList.add(new StatusEntity().setType(StatusEntity.TYPE_BODY));
        arrayList.add(new StatusEntity().setType(StatusEntity.TYPE_BODY));
        arrayList.add(new StatusEntity().setType(StatusEntity.TYPE_BODY));
        arrayList.add(new StatusEntity().setType(StatusEntity.TYPE_BODY));
        int bodyPosition = -1;
        for (StatusEntity statusEntity : arrayList) {
            if (statusEntity.type == StatusEntity.TYPE_BODY) {
                if (bodyPosition == -1) {
                    bodyPosition = 0;
                } else {
                    bodyPosition++;
                }
                statusEntity.setBodyPosition(bodyPosition);
            } else {
                bodyPosition = -1;
            }
        }
        return arrayList;
    }

    @Override
    public void initData() {

    }
}