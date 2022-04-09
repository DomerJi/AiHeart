package com.thfw.mobileheart.activity.mood;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.thfw.base.base.IPresenter;
import com.thfw.base.face.OnRvItemListener;
import com.thfw.base.models.StatusEntity;
import com.thfw.base.utils.LogUtil;
import com.thfw.base.utils.ToastUtil;
import com.thfw.mobileheart.R;
import com.thfw.mobileheart.activity.BaseActivity;
import com.thfw.mobileheart.adapter.StatusAdapter;
import com.thfw.mobileheart.util.DialogFactory;
import com.thfw.ui.dialog.TDialog;
import com.thfw.ui.dialog.base.BindViewHolder;
import com.thfw.ui.dialog.listener.OnBindViewListener;
import com.thfw.ui.dialog.listener.OnViewClickListener;
import com.thfw.ui.widget.LinearTopLayout;
import com.thfw.ui.widget.LoadingView;
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
    private LoadingView mLoadingView;

    public static void startActivity(Context context) {
        context.startActivity(new Intent(context, StatusActivity.class));
    }

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
        mTitleView.setRightText("历史心情");
        mTitleView.getTvRight().setOnClickListener(v -> {
            MoodDetailActivity.startActivity(mContext);
        });
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
        mStatusAdapter.setOnRvItemListener(new OnRvItemListener<StatusEntity>() {
            @Override
            public void onItemClick(List<StatusEntity> list, int position) {

            }
        });
        mRvStatusList.setAdapter(mStatusAdapter);

        mLoadingView = findViewById(R.id.loadingView);
        mLoadingView.hide();
//        mIvBlurBg = (ImageView) findViewById(R.id.iv_blur_bg);
//        Bitmap bitmap = BitmapUtil.getResourceBitmap(mContext, R.mipmap.cat);
//        mIvBlurBg.setImageBitmap(PaletteUtil.doBlur(bitmap, 30, true));
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

    private void customStatus() {
        DialogFactory.createCustomStatus(StatusActivity.this, new OnBindViewListener() {
            @Override
            public void bindView(BindViewHolder viewHolder) {
                EditText editText = viewHolder.getView(R.id.et_custom_status);
                View clearEdit = viewHolder.getView(R.id.iv_clear_edit);
                clearEdit.setOnClickListener(v -> {
                    editText.setText("");
                });
                editText.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        clearEdit.setVisibility(s.length() > 0 ? View.VISIBLE : View.GONE);
                    }
                });
            }
        }, new OnViewClickListener() {
            @Override
            public void onViewClick(BindViewHolder viewHolder, View view, TDialog tDialog) {
                if (view.getId() == R.id.tv_confirm) {
                    EditText editText = viewHolder.getView(R.id.et_custom_status);
                    ToastUtil.show(editText.getText().toString());
                    tDialog.dismiss();
                } else {
                    tDialog.dismiss();
                }
            }
        });
    }
}