package com.zhongke.market.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.RelativeLayout;

import com.zhongke.market.R;
import com.zhongke.market.gui.view.BaseTitleView;


/**
 * Created by llj on 2017/6/16.
 */

public abstract class BaseActivityOld extends AppCompatActivity {
    private static final String TAG = "BaseActivity";
    protected RelativeLayout loadingLay;
    protected RelativeLayout noDataLay;
    protected RelativeLayout errorLay;
    protected RelativeLayout centerLay;
    protected BaseTitleView baseTitle;
//    /** 是否设置状态栏颜色*/
//    protected boolean isSetStatusColor = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

//        if(isSetStatusColor){
//            Tools.setStatusColor(this, getResources().getColor(R.color.main_color));
//        }

        loadingLay = (RelativeLayout) findViewById(R.id.base_loading_lay);
        noDataLay = (RelativeLayout) findViewById(R.id.base_no_data_lay);
        errorLay = (RelativeLayout) findViewById(R.id.base_error_lay);
        centerLay = (RelativeLayout) findViewById(R.id.base_content_lay);
        baseTitle = (BaseTitleView) findViewById(R.id.base_title_lay);

//        ComponentHolder.getAppComponent().inject(this);
        showLoadingView();
//        Log.e(TAG, "---------->onCreate" + mDataManager);

        errorLay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tryAgain();
            }
        });
    }

    /**
     * 添加中间正文视图
     *
     * @param resId 本地布局文件资源id
     */
    protected void setCenterLay(int resId) {
        centerLay.removeAllViews();
        centerLay.addView(View.inflate(this, resId, null), RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
    }

    /**
     * 添加中间正文视图
     *
     * @param view 子视图
     */
    protected void setCenterLay(View view) {
        centerLay.removeAllViews();
        centerLay.addView(view, RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
    }


    /**
     * 显示loading视图
     */
    protected void showLoadingView() {
        loadingLay.setVisibility(View.VISIBLE);
        noDataLay.setVisibility(View.GONE);
        errorLay.setVisibility(View.GONE);
        centerLay.setVisibility(View.GONE);
    }

    /**
     * 显示无数据视图
     */
    protected void showNoDataView() {
        loadingLay.setVisibility(View.GONE);
        noDataLay.setVisibility(View.VISIBLE);
        errorLay.setVisibility(View.GONE);
        centerLay.setVisibility(View.GONE);
    }

    /**
     * 显示错误视图
     */
    protected void showErrorView() {
        loadingLay.setVisibility(View.GONE);
        noDataLay.setVisibility(View.GONE);
        errorLay.setVisibility(View.VISIBLE);
        centerLay.setVisibility(View.GONE);
    }

    /**
     * 显示正文视图
     */
    protected void showCenterView() {
        loadingLay.setVisibility(View.GONE);
        noDataLay.setVisibility(View.GONE);
        errorLay.setVisibility(View.GONE);
        centerLay.setVisibility(View.VISIBLE);
    }

    /**
     * 设置界面标题名称
     *
     * @param title
     */
    protected void setTitleName(String title) {
        baseTitle.setTitleName(title);
    }


    /**
     * 界面重新加载方法
     */
    protected void tryAgain(){
        showLoadingView();
    }

//    /** 初始化视图*/
//    protected abstract void initView();
}
