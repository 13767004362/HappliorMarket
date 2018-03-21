package com.zhongke.market.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import rx.subscriptions.CompositeSubscription;

/**
 * Created by ${xingen} on 2017/7/5.
 */

public  abstract class BaseFragment  extends Fragment {


    /**
     *  rx的订阅者管理器
     */
    protected CompositeSubscription compositeSubscription;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.compositeSubscription=new CompositeSubscription();
    }
    private View rootView;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.rootView=inflater.inflate(getLayoutId(),container,false);
        return rootView ;
    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initAfterActivityCreated(rootView,savedInstanceState);
    }
    @Override
    public void onDestroy() {
        if (compositeSubscription!=null){
            compositeSubscription.clear();
        }
        super.onDestroy();
    }
    /**
     * 获取制定的布局id
     * @return
     */
    protected abstract int getLayoutId();

    /**
     *  Activity被创建后
     * @param rootView
     * @param savedInstanceState
     */
    protected abstract void initAfterActivityCreated( View rootView,Bundle savedInstanceState);
}
