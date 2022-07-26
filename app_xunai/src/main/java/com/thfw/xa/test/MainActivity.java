package com.thfw.xa.test;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.thfw.base.face.OnRvItemListener;
import com.thfw.ui.widget.TitleView;
import com.thfw.xa.test.adapter.HomeAdapter;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private TitleView mTitleView;
    private RecyclerView mRvAction;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.activity_main);

        initView();
    }

    private void initView() {
        mTitleView = (TitleView) findViewById(R.id.titleView);
        mRvAction = (RecyclerView) findViewById(R.id.rv_action);
        mRvAction.setLayoutManager(new LinearLayoutManager(mContext));

        String[] titles = new String[]{"舌诊", "面诊", "手诊"};
        HomeAdapter mHomeAdapter = new HomeAdapter(Arrays.asList(titles));
        mHomeAdapter.setOnRvItemListener(new OnRvItemListener<String>() {
            @Override
            public void onItemClick(List<String> list, int position) {
                switch (position) {
                    case 0:
                        startActivity(new Intent(mContext, TongueActivity.class));
                        break;
                    case 1:
                        startActivity(new Intent(mContext, FaceActivity.class));
                        break;
                    case 2:
                        startActivity(new Intent(mContext, HandleActivity.class));
                        break;
                }
            }
        });
        mRvAction.setAdapter(mHomeAdapter);
    }
}